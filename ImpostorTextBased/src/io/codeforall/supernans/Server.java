package io.codeforall.supernans;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Server {

    ServerSocket serverSocket = new ServerSocket(8080);
    Socket clientSocket;
    LinkedList<ServerWorker> serverWorkerList = new LinkedList<>();

    ServerWorker serverWorker;
    Set<String> startGameSet = new HashSet<>();

    Set<String> playerNamesSet = new HashSet<>();


    HashMap<String, Integer> voteResults = new HashMap<>();


    int numPlayers;
    String mostVotedPlayer;
    ImpostorGame impostorGame;
    boolean promptGiven = false;

    public Server(int maxPlayers) throws IOException {


        this.numPlayers = maxPlayers;

    }

    public synchronized void eliminatePlayer() throws IOException {
        playerNamesSet.remove(mostVotedPlayer);
        for (ServerWorker serverWorker : serverWorkerList) {
            if (mostVotedPlayer == serverWorker.name && serverWorker.impostor)
                for (ServerWorker serverWorker2 : serverWorkerList) {
                    serverWorker2.eliminatedImposterAnounce = true;
                }
            if (mostVotedPlayer == serverWorker.name && !serverWorker.impostor) {
                for (ServerWorker serverWorker2 : serverWorkerList) {
                    serverWorker2.eliminatedPlayerAnounce = true;
                }
            }
        }
        for (ServerWorker serverWorker1 : serverWorkerList) {
            if (serverWorker1.name == mostVotedPlayer) {
                serverWorkerList.remove(serverWorker);
            }
        }

    }

    public synchronized String countVotes() throws IOException {

        String mostVotedName = new String();
        Integer mostVotes = 0;
        for (String name : playerNamesSet) {
            if (mostVotes < voteResults.get(name)) {
                mostVotedName = name;
                mostVotes = voteResults.get(name);
            }
        }
        for (String name : playerNamesSet) {
            if (mostVotes == voteResults.get(name) && name != mostVotedName) {
                for (ServerWorker serverWorker : serverWorkerList) {
                    serverWorker.draw = true;
                }
            } else {
                for (ServerWorker serverWorker1 : serverWorkerList) {
                    serverWorker1.finalmente = true;
                }

            }
        }


        return mostVotedName;


    }

    public synchronized boolean FINALFINALMENTE() {
        for (ServerWorker serverWorker : serverWorkerList) {
            if (!serverWorker.finalmente) {
                return false;
            }
        }
        return true;
    }

    public synchronized boolean arePlayersReady() {
        for (ServerWorker serverWorker : serverWorkerList) {
            if (!serverWorker.ready) {
                return false;
            }
        }
        return true;
    }

    public synchronized boolean haveAllPlayersVoted() {
        for (ServerWorker serverWorker : serverWorkerList) {
            if (!serverWorker.voted) {
                return false;
            }
        }
        return true;
    }

    public synchronized boolean arePlayersOutOfClues() {
        for (ServerWorker serverWorker : serverWorkerList) {
            if (!serverWorker.outOfClues) {
                return false;
            }
        }
        return true;
    }

    public synchronized void listen() {
        while (true) {

            if (serverWorkerList.size() == numPlayers) {
                if (arePlayersReady()) {
                    impostorGame = new ImpostorGame(this);
                    notifyAll();
                    break;
                }
            } else {
                try {
                    clientSocket = serverSocket.accept();
                    Thread thread = new Thread(serverWorker = new ServerWorker(this));
                    serverWorkerList.add(serverWorker);
                    thread.start();

                } catch (IOException e) {
                    System.out.println("Couldn't connect");
                }
            }
        }

    }


}
