package client;

import java.util.Scanner;

import static ui.EscapeSequences.*;

// This is a comment
public class Repl {
    PreLoginClient preLoginClient;
    PostLoginClient postLoginClient;
    PlayChessClient playChessClient;
    private State state = State.LOGGED_OUT;

    public Repl(String serverUrl) {
        preLoginClient = new PreLoginClient(serverUrl);
        postLoginClient = new PostLoginClient(serverUrl);
        playChessClient = new PlayChessClient(serverUrl);
    }

    public void run() {
        ChessClient client = preLoginClient;
        System.out.println("\uD83D\uDC36 Welcome to Chess. Sign in to start.");
        System.out.print(SET_TEXT_COLOR_BLUE + preLoginClient.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = client.eval(line);
                if (result.contains("You signed in")) {
                    client = postLoginClient;
                    state = State.LOGGED_IN;
                } else if (result.contains("You signed out")) {
                    client = preLoginClient;
                    state = State.LOGGED_OUT;
                } else if (result.contains("You join game") || result.contains("You observe game")) {
                    client = playChessClient;
                    state = State.PLAY_CHESS;
                } else if (result.contains("You left the game")) {
                    client = postLoginClient;
                    state = State.LOGGED_IN;
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
        String stateString;
        switch (state) {
            case LOGGED_IN -> stateString = "LOGGED_IN";
            case PLAY_CHESS -> stateString = "PLAY_CHESS";
            default -> stateString = "LOGGED_OUT";
        }
        System.out.print("\n" + RESET_TEXT_COLOR + stateString + " >>> " + SET_TEXT_COLOR_GREEN);
    }
}
