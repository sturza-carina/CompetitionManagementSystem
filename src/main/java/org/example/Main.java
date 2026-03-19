package org.example;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.example.domain.Participant;
import org.example.repository.databases.ParticipantDBRepo;
import org.example.utils.DBUtils;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;


public class Main {
    private static final Logger log = LogManager.getLogger(Main.class);

    static void main() {
        // configurare log4j
        Properties props = new Properties();
        try {
            props.load(new FileReader("bd.config"));
        } catch (IOException e) {
            System.out.println("Cannot find bd.config " + e);
        }

        Main.log.info("Starting application...");

        DBUtils dbUtils = new DBUtils(props);

        ParticipantDBRepo participantRepo = new ParticipantDBRepo(dbUtils);

        try {
            // adaugare participant
            Participant p = new Participant("Matei", "9864728361538", 20);
            participantRepo.add(p);
            Main.log.info("Participant added!");

            // cautare dupa CNP
            Participant found = participantRepo.findByCnp("9864728361538");

            if (found != null) {
                Main.log.info("Found: " + found.getNume() +
                        ", CNP: " + found.getCnp() +
                        ", Varsta: " + found.getVarsta());
            } else {
                Main.log.warn("Participant not found!");
            }

            // afisare toti
            Main.log.info("All participants:");
            for (Participant part : participantRepo.findAll()) {
                Main.log.info(part.getId() + " | " +
                        part.getNume() + " | " +
                        part.getCnp() + " | " +
                        part.getVarsta());
            }

        } catch (Exception ex) {
            Main.log.error("Error: " + ex.getMessage());
        } finally {
            dbUtils.closeConnection();
        }
    }
}
