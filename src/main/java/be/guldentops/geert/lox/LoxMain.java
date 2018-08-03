package be.guldentops.geert.lox;

import be.guldentops.geert.lox.error.ErrorReporter;

import java.io.IOException;

/**
 * This class is NOT tested since it:
 *
 * * Exposes the static method main which makes it harder to test.
 * * When errors occur or the program is incorrectly used it performs a System.exit which is hard to test.
 *
 * Because it is not tested its logic is kept to a bare minimum.
 */
class LoxMain {

    public static void main(String[] args) throws IOException {
        var errorReporter = ErrorReporter.console();
        var lox = new Lox(errorReporter);

        if (args.length > 1) {
            System.out.println("Usage: jlox [script]");
            System.exit(64);
        } else if (args.length == 1) {
            lox.runFile(args[0]);

            // Indicate an error in the exit code.
            if (errorReporter.receivedError()) System.exit(65);
            if (errorReporter.receivedRuntimeError()) System.exit(70);
        } else {
            lox.runPrompt();
        }
    }
}
