package io.codeforall.supernans;

import java.io.BufferedWriter;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println(Thread.currentThread().getName());
        Server impostorServer = new Server(4);
        impostorServer.listen(); //Só saimos daqui quando está toda a gente ready!
        synchronized (Thread.currentThread()) {
            impostorServer.impostorGame.choseImpostors();
            impostorServer.impostorGame.getAPrompt();
        }
        while (true) {
            synchronized (Thread.currentThread()) {
                System.out.println(impostorServer.haveAllPlayersVoted());
                if (impostorServer.haveAllPlayersVoted()) {
                    impostorServer.mostVotedPlayer = impostorServer.countVotes();
                    System.out.println(impostorServer.FINALFINALMENTE());
                    if (impostorServer.FINALFINALMENTE()) {
                        impostorServer.eliminatePlayer();
                        return;
                    }
                }

            }


        }


    }
}

