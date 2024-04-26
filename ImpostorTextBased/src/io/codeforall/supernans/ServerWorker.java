package io.codeforall.supernans;

import org.academiadecodigo.bootcamp.Prompt;
import org.academiadecodigo.bootcamp.scanners.string.StringInputScanner;
import org.academiadecodigo.bootcamp.scanners.string.StringSetInputScanner;

import java.io.*;

public class ServerWorker implements Runnable {

     volatile boolean eliminatedImposterAnounce = false;
    volatile boolean eliminatedPlayerAnounce = false;
    BufferedReader bufferedReader;

    BufferedWriter bufferedWriter;

    Server server;
    PrintStream outPrintStream;

    String name;
    Prompt prompt;
    int counterTries = 0;
   volatile boolean finalmente = false;

   volatile boolean voted = false;

   volatile boolean draw = false;
   volatile boolean impostor = false;
   volatile boolean ready = false;
   volatile boolean outOfClues = false;

    StringInputScanner votes = new StringInputScanner();
    StringInputScanner nameQuestion = new StringInputScanner();
    StringInputScanner promptClue = new StringInputScanner();

    public ServerWorker(Server server) throws IOException {
        this.server = server;
        bufferedReader = new BufferedReader(new InputStreamReader(server.clientSocket.getInputStream()));
        bufferedWriter = new BufferedWriter(new OutputStreamWriter(server.clientSocket.getOutputStream()));
        outPrintStream = new PrintStream(server.clientSocket.getOutputStream(), true);


        prompt = new Prompt(server.clientSocket.getInputStream(), outPrintStream);

    }

    @Override
    public void run() {

        System.out.println(Thread.currentThread().getName());
        try {
            welcome();
            getPlayerNames();
            getReady();
            synchronized (this) {
                while (true) {
                    System.out.println(server.promptGiven);
                    System.out.println(counterTries);
                    if (server.promptGiven && counterTries < 3) {
                        startRound();
                    }
                    System.out.println(server.arePlayersOutOfClues());
                    if (server.arePlayersOutOfClues()) {
                        System.out.println(voted);
                        System.out.println(draw);
                        synchronized (this) {
                            if (!voted) {
                                votingPhase();
                            }
                            if(draw)
                            {bufferedWriter.write("██████╗ ██████╗  █████╗ ██╗    ██╗██╗\n" +
                                    "██╔══██╗██╔══██╗██╔══██╗██║    ██║██║\n" +
                                    "██║  ██║██████╔╝███████║██║ █╗ ██║██║\n" +
                                    "██║  ██║██╔══██╗██╔══██║██║███╗██║╚═╝\n" +
                                    "██████╔╝██║  ██║██║  ██║╚███╔███╔╝██╗\n" +
                                    "╚═════╝ ╚═╝  ╚═╝╚═╝  ╚═╝ ╚══╝╚══╝ ╚═╝\n" +
                                    "                                     ");
                                bufferedWriter.flush();
                                return;

                            }
                        }
                        System.out.print(eliminatedImposterAnounce);
                        if (eliminatedImposterAnounce) {
                            bufferedWriter.write(server.mostVotedPlayer + " was eliminated!\n ");
                            bufferedWriter.flush();
                            bufferedWriter.write("██████╗ ██╗      █████╗ ██╗   ██╗███████╗██████╗ ███████╗\n" +
                                    "██╔══██╗██║     ██╔══██╗╚██╗ ██╔╝██╔════╝██╔══██╗██╔════╝\n" +
                                    "██████╔╝██║     ███████║ ╚████╔╝ █████╗  ██████╔╝███████╗\n" +
                                    "██╔═══╝ ██║     ██╔══██║  ╚██╔╝  ██╔══╝  ██╔══██╗╚════██║\n" +
                                    "██║     ███████╗██║  ██║   ██║   ███████╗██║  ██║███████║\n" +
                                    "╚═╝     ╚══════╝╚═╝  ╚═╝   ╚═╝   ╚══════╝╚═╝  ╚═╝╚══════╝\n" +
                                    "                                                         \n" +
                                    "    ██╗    ██╗ ██████╗ ███╗   ██╗██╗                     \n" +
                                    "    ██║    ██║██╔═══██╗████╗  ██║██║                     \n" +
                                    "    ██║ █╗ ██║██║   ██║██╔██╗ ██║██║                     \n" +
                                    "    ██║███╗██║██║   ██║██║╚██╗██║╚═╝                     \n" +
                                    "    ╚███╔███╔╝╚██████╔╝██║ ╚████║██╗                     \n" +
                                    "     ╚══╝╚══╝  ╚═════╝ ╚═╝  ╚═══╝╚═╝                     \n" +
                                    "                                                         ");
                            bufferedWriter.flush();
                            eliminatedImposterAnounce = false;
                            return;
                        }
                        System.out.print(eliminatedPlayerAnounce);
                        if (eliminatedPlayerAnounce) {
                            bufferedWriter.write(server.mostVotedPlayer + " was eliminated!\n ");
                            bufferedWriter.flush();
                            bufferedWriter.write("██╗███╗   ███╗██████╗  ██████╗ ███████╗████████╗ ██████╗ ██████╗ \n" +
                                    "██║████╗ ████║██╔══██╗██╔═══██╗██╔════╝╚══██╔══╝██╔═══██╗██╔══██╗\n" +
                                    "██║██╔████╔██║██████╔╝██║   ██║███████╗   ██║   ██║   ██║██████╔╝\n" +
                                    "██║██║╚██╔╝██║██╔═══╝ ██║   ██║╚════██║   ██║   ██║   ██║██╔══██╗\n" +
                                    "██║██║ ╚═╝ ██║██║     ╚██████╔╝███████║   ██║   ╚██████╔╝██║  ██║\n" +
                                    "╚═╝╚═╝     ╚═╝╚═╝      ╚═════╝ ╚══════╝   ╚═╝    ╚═════╝ ╚═╝  ╚═╝\n" +
                                    "                                                                 \n" +
                                    "    ██╗    ██╗ ██████╗ ███╗   ██╗██╗                             \n" +
                                    "    ██║    ██║██╔═══██╗████╗  ██║██║                             \n" +
                                    "    ██║ █╗ ██║██║   ██║██╔██╗ ██║██║                             \n" +
                                    "    ██║███╗██║██║   ██║██║╚██╗██║╚═╝                             \n" +
                                    "    ╚███╔███╔╝╚██████╔╝██║ ╚████║██╗                             \n" +
                                    "     ╚══╝╚══╝  ╚═════╝ ╚═╝  ╚═══╝╚═╝                             \n" +
                                    "                                                                 ");
                            bufferedWriter.flush();
                            eliminatedImposterAnounce = false;
                            return;
                        }

                    }

                }
            }


        } catch (IOException | InterruptedException e) {
            System.out.println("IO Exception");
        }


    }


    public synchronized void startRound() throws IOException {
        promptClue.setMessage("\nTell us something about the prompt...\n");
        String clue = prompt.getUserInput(promptClue);
        for (ServerWorker serverWorker : server.serverWorkerList) {
            if (!this.equals(serverWorker)) {
                serverWorker.bufferedWriter.write(name + " said: " + clue);
                serverWorker.bufferedWriter.newLine();
                serverWorker.bufferedWriter.flush();

            }
        }
        counterTries++;
        if (counterTries == 3) {
            outOfClues = true;
        }
    }

    public void votingPhase() throws IOException {
        draw = false;
        voted = false;
        votes = new StringSetInputScanner(server.playerNamesSet);
        votes.setError("This is not a name of a player... Please write the name of a player, be careful with case sensitivity!");

        votes.setMessage("Please vote on who you think is the imposter.");
        String vote = prompt.getUserInput(votes);
        if (server.playerNamesSet.contains(vote)) {
            voted = true;
            server.voteResults.put(vote, server.voteResults.get(vote) + 1);
            bufferedWriter.write("You have voted, waiting for other player to vote...\n");
            bufferedWriter.flush();
        }

    }

    public synchronized void getPlayerNames() throws IOException {
        nameQuestion.setMessage("What is your name?");
        name = prompt.getUserInput(nameQuestion);
        bufferedWriter.write(name);
        bufferedWriter.flush();
        server.playerNamesSet.add(name);
        server.voteResults.put(name, 0);
        System.out.println(server.playerNamesSet.size());
    }

    public synchronized void welcome() throws IOException {
        bufferedWriter.write("    ████████╗██╗  ██╗███████╗    ██╗███╗   ███╗██████╗  ██████╗ ███████╗████████╗ ██████╗ ██████╗     \n" +
                "    ╚══██╔══╝██║  ██║██╔════╝    ██║████╗ ████║██╔══██╗██╔═══██╗██╔════╝╚══██╔══╝██╔═══██╗██╔══██╗    \n" +
                "       ██║   ███████║█████╗      ██║██╔████╔██║██████╔╝██║   ██║███████╗   ██║   ██║   ██║██████╔╝    \n" +
                "       ██║   ██╔══██║██╔══╝      ██║██║╚██╔╝██║██╔═══╝ ██║   ██║╚════██║   ██║   ██║   ██║██╔══██╗    \n" +
                "       ██║   ██║  ██║███████╗    ██║██║ ╚═╝ ██║██║     ╚██████╔╝███████║   ██║   ╚██████╔╝██║  ██║    \n" +
                "       ╚═╝   ╚═╝  ╚═╝╚══════╝    ╚═╝╚═╝     ╚═╝╚═╝      ╚═════╝ ╚══════╝   ╚═╝    ╚═════╝ ╚═╝  ╚═╝    \n" +
                "                                                                                                      ");
        bufferedWriter.newLine();
        bufferedWriter.flush();
    }

    public synchronized void getReady() throws IOException, InterruptedException {

        server.startGameSet.add("yes");
        server.startGameSet.add("Y");
        server.startGameSet.add("y");
        server.startGameSet.add("yES");
        server.startGameSet.add("YES");
        server.startGameSet.add("Yes");
        StringSetInputScanner getReady = new StringSetInputScanner(server.startGameSet);
        getReady.setError("Ok... when ready write \"yes\"");
        getReady.setMessage("\n \n General rules:\n" +
                "- Minimum of 4 players\n" +
                "- All players receive the name of a series or film, except the impostor.\n" +
                "- Each player must say a word related to the film/series, including the impostor, who must be aware of the other words and also get into the theme.\n -IMPOSTOR NOTE: YOUR MISSION IS TO FOOL EVERYONE\n" +
                "- After 3 turns, the players must vote for who they think is the impostor.\n" +
                "- The player with the most votes is eliminated from the game.\n" +
                "- Impostors Win if a player is kicked!\n" +
                "- Players Win if the Impostor is kicked!\n" +
                "- If two players have the same number of votes the game ends in a DRAW!\n" +
                "\n █████╗ ██████╗ ███████╗    ██╗   ██╗ ██████╗ ██╗   ██╗    ██████╗ ███████╗ █████╗ ██████╗ ██╗   ██╗██████╗ \n" +
                "██╔══██╗██╔══██╗██╔════╝    ╚██╗ ██╔╝██╔═══██╗██║   ██║    ██╔══██╗██╔════╝██╔══██╗██╔══██╗╚██╗ ██╔╝╚════██╗\n" +
                "███████║██████╔╝█████╗       ╚████╔╝ ██║   ██║██║   ██║    ██████╔╝█████╗  ███████║██║  ██║ ╚████╔╝   ▄███╔╝\n" +
                "██╔══██║██╔══██╗██╔══╝        ╚██╔╝  ██║   ██║██║   ██║    ██╔══██╗██╔══╝  ██╔══██║██║  ██║  ╚██╔╝    ▀▀══╝ \n" +
                "██║  ██║██║  ██║███████╗       ██║   ╚██████╔╝╚██████╔╝    ██║  ██║███████╗██║  ██║██████╔╝   ██║     ██╗   \n" +
                "╚═╝  ╚═╝╚═╝  ╚═╝╚══════╝       ╚═╝    ╚═════╝  ╚═════╝     ╚═╝  ╚═╝╚══════╝╚═╝  ╚═╝╚═════╝    ╚═╝     ╚═╝   \n" +
                "                                                                                                             (yes or no)");
        String playerIsReady = prompt.getUserInput(getReady);
        if (server.startGameSet.contains(playerIsReady)) {
            ready = true;
            bufferedWriter.write("You are waiting for the other players...\n");
            bufferedWriter.flush();


        }
    }


}