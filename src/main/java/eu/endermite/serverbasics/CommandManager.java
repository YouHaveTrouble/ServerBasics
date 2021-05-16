package eu.endermite.serverbasics;

import cloud.commandframework.CommandTree;
import cloud.commandframework.annotations.AnnotationParser;
import cloud.commandframework.arguments.parser.ParserParameters;
import cloud.commandframework.arguments.parser.StandardParameters;
import cloud.commandframework.bukkit.BukkitCommandManager;
import cloud.commandframework.bukkit.CloudBukkitCapabilities;
import cloud.commandframework.exceptions.NoPermissionException;
import cloud.commandframework.execution.AsynchronousCommandExecutionCoordinator;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.extra.confirmation.CommandConfirmationManager;
import cloud.commandframework.meta.CommandMeta;
import cloud.commandframework.paper.PaperCommandManager;
import eu.endermite.serverbasics.commands.registration.SyncCommandRegistration;
import eu.endermite.serverbasics.messages.MessageParser;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.reflections.Reflections;
import eu.endermite.serverbasics.commands.registration.CommandRegistration;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class CommandManager {

    private final ServerBasics plugin = ServerBasics.getInstance();

    public BukkitCommandManager<CommandSender> manager;
    public BukkitCommandManager<CommandSender> syncManager;
    private CommandConfirmationManager<CommandSender> confirmationManager;
    @Getter private AnnotationParser<CommandSender> annotationParser, syncAnnotationParser;

    public void initCommands() {
        final Function<CommandTree<CommandSender>, CommandExecutionCoordinator<CommandSender>> executionCoordinatorFunction =
                AsynchronousCommandExecutionCoordinator.<CommandSender>newBuilder().build();
        final Function<CommandTree<CommandSender>, CommandExecutionCoordinator<CommandSender>> syncExecutionCoordinatorFunction =
                CommandExecutionCoordinator.simpleCoordinator();

        final Function<CommandSender, CommandSender> mapperFunction = Function.identity();
        try {
            manager = new PaperCommandManager<>(
                    /* Owning plugin */ plugin,
                    /* Coordinator function */ executionCoordinatorFunction,
                    /* Command Sender -> C */ mapperFunction,
                    /* C -> Command Sender */ mapperFunction
            );
            syncManager = new PaperCommandManager<>(
                    /* Owning plugin */ plugin,
                    /* Coordinator function */ syncExecutionCoordinatorFunction,
                    /* Command Sender -> C */ mapperFunction,
                    /* C -> Command Sender */ mapperFunction
            );
        } catch (final Exception e) {
            plugin.getLogger().severe("Failed to initialize the command manager");
            plugin.getServer().getPluginManager().disablePlugin(plugin);
            return;
        }

        if (manager.queryCapability(CloudBukkitCapabilities.BRIGADIER)) {
            manager.registerBrigadier();
        }
        if (syncManager.queryCapability(CloudBukkitCapabilities.BRIGADIER)) {
            syncManager.registerBrigadier();
        }

        if (manager.queryCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION)) {
            ((PaperCommandManager<CommandSender>) manager).registerAsynchronousCompletions();
        }
        if (syncManager.queryCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION)) {
            ((PaperCommandManager<CommandSender>) syncManager).registerAsynchronousCompletions();
        }

        confirmationManager = new CommandConfirmationManager<>(
                /* Timeout */ 30L,
                /* Timeout unit */ TimeUnit.SECONDS,
                /* Action when confirmation is required */ context -> context.getCommandContext().getSender().sendMessage(
                ChatColor.RED + "Confirmation required. Confirm using /example confirm."),
                /* Action when no confirmation is pending */ sender -> sender.sendMessage(
                ChatColor.RED + "You don't have any pending commands.")
        );
        final Function<ParserParameters, CommandMeta> commandMetaFunction = p ->
                CommandMeta.simple()
                        .with(CommandMeta.DESCRIPTION, p.get(StandardParameters.DESCRIPTION, "No description"))
                        .build();
        annotationParser = new AnnotationParser<>(
                manager,
                CommandSender.class,
                commandMetaFunction
        );
        syncAnnotationParser = new AnnotationParser<>(
                syncManager,
                CommandSender.class,
                commandMetaFunction
        );

        // Command error messages
        //TODO find which errors have according message in client and use that if possible
        manager.registerExceptionHandler(NoPermissionException.class, (sender, exception) -> {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                MessageParser.sendMessage(sender, ServerBasics.getLang(player.getLocale()).no_permission);
            } else {
                MessageParser.sendMessage(sender, ServerBasics.getLang(ServerBasics.getConfigCache().default_lang).no_permission);
            }
        });


        constructCommands();
        construcSyncCommands();
    }

    private void constructCommands() {
        Reflections reflections = new Reflections((Object) new String[]{"eu.endermite.serverbasics.commands"});
        Set<Class<?>> listenerClasses = reflections.getTypesAnnotatedWith(CommandRegistration.class);
        listenerClasses.forEach((command)-> {
            try {
                ServerBasics.getCommandManager().getAnnotationParser().parse(command.getConstructor().newInstance()) ;
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        });
    }
    private void construcSyncCommands() {
        Reflections reflections = new Reflections((Object) new String[]{"eu.endermite.serverbasics.commands"});
        Set<Class<?>> listenerClasses = reflections.getTypesAnnotatedWith(SyncCommandRegistration.class);
        listenerClasses.forEach((command)-> {
            try {
                ServerBasics.getCommandManager().getSyncAnnotationParser().parse(command.getConstructor().newInstance()) ;
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        });
    }

}
