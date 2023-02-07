package dev.ebullient.pockets;

import java.util.concurrent.Callable;

import javax.enterprise.context.control.ActivateRequestContext;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import dev.ebullient.pockets.index.Index;
import dev.ebullient.pockets.io.PocketTui;
import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.ExitCode;
import picocli.CommandLine.IFactory;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParseResult;
import picocli.CommandLine.ScopeType;
import picocli.CommandLine.Spec;

@QuarkusMain
@Command(name = "pockets", header = "What have you got in your pockets?", subcommands = {
        PocketList.class,
        PocketCreate.class,
        PocketDelete.class,
        PocketEdit.class,
        Coins.class,
        ItemAdd.class,
        ItemRemove.class,
        ItemUpdate.class,
        PocketsImport.class }, mixinStandardHelpOptions = true, sortOptions = false, headerHeading = "%n", synopsisHeading = "%nUsage: ", parameterListHeading = "%nParameters:%n", optionListHeading = "%nOptions:%n", commandListHeading = "%nCommands:%n", scope = ScopeType.INHERIT)
public class PocketsCli implements Callable<Integer>, QuarkusApplication {

    final PocketTui tui = new PocketTui();
    final Index index = new Index(tui);

    @Spec
    CommandSpec spec;

    @Inject
    IFactory factory;

    @Option(names = { "-d", "--debug" }, description = "Enable debug output", scope = ScopeType.INHERIT)
    boolean debug;

    @Option(names = { "-b", "--brief" }, description = "Brief output", scope = ScopeType.INHERIT)
    boolean brief;

    @Option(names = { "-i",
            "--interactive" }, description = "Confirm and prompt for missing arguments.", required = false, help = true, scope = ScopeType.INHERIT)
    boolean interactive = false;

    @Override
    public Integer call() throws Exception {
        // invocation of `pockets` command with no other command specified.
        // Point to the web ui and wait..
        tui.outPrintln("");
        tui.outPrintln("🛍️  What have you got in your pockets?");
        tui.outPrintln("");
        tui.outPrintln("Visit the UI: http://localhost:" + System.getProperty("quarkus.http.port"));
        tui.outPrintln("");
        tui.outPrintln("⏱️  Waiting until you're done...   Use [Ctrl/Cmd-C] to cancel");
        tui.outPrintln("");
        tui.outPrintln(
                "🔹 This is a locally running process that does not gather any information about you or your usage. Use --help for other commands and options.");

        Quarkus.waitForExit();

        return ExitCode.OK;
    }

    private int executionStrategy(ParseResult parseResult) {
        try {
            init(parseResult);
            return new CommandLine.RunLast().execute(parseResult);
        } finally {
            shutdown();
        }
    }

    private void init(ParseResult parseResult) {
        tui.init(spec, debug, !brief, interactive);
        index.init();
        tui.format().setIndex(index); // reference for formatting
    }

    private void shutdown() {
        tui.close();
    }

    @Override
    @ActivateRequestContext
    public int run(String... args) throws Exception {
        return new CommandLine(this, factory)
                .setCaseInsensitiveEnumValuesAllowed(true)
                .setExecutionStrategy(this::executionStrategy)
                .execute(args);
    }

    @Produces
    PocketTui getTui() {
        return tui;
    }

    @Produces
    Index getIndex() {
        return index;
    }
}
