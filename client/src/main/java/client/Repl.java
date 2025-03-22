package client;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Repl {
    AnteLoginClient anteLoginClient;
    PostLoginClient postLoginClient;
    PlayChessClient playChessClient;

    public Repl(String serverUrl) {
        anteLoginClient = new AnteLoginClient(serverUrl);
        postLoginClient = new PostLoginClient(serverUrl);
        playChessClient = new PlayChessClient(serverUrl);
    }

    public void run() {
        ChessClient client = anteLoginClient;
        System.out.println("\uD83D\uDC36 Welcome to Chess. Sign in to start.");
        System.out.print(SET_TEXT_COLOR_BLUE + anteLoginClient.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = client.eval(line);
                if (result.contains("You signed in")) {
                    client = postLoginClient;
                    System.out.print(SET_TEXT_COLOR_BLUE + client.help());
                } else if (result.contains("You signed out")) {
                    client = anteLoginClient;
                    System.out.print(SET_TEXT_COLOR_BLUE + client.help());
                }
                System.out.print(SET_TEXT_COLOR_BLUE + result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    private void printPrompt() {
        System.out.print("\n" + RESET_TEXT_COLOR + ">>> " + SET_TEXT_COLOR_GREEN);
    }
}
