package wtf.accounts;

import com.google.gson.*;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AccountManager {

    public static String save(String username, String password, boolean cracked, String location) {
        try {
            File accountsFile = new File(location + "/accounts.json");

            // If the file does not exist, create an empty JSON object
            JsonObject rootObject;
            if (accountsFile.exists()) {
                try (FileReader reader = new FileReader(accountsFile)) {
                    rootObject = new Gson().fromJson(reader, JsonObject.class);
                }
            } else {
                rootObject = new JsonObject();
            }

            // Create an account object with username, password, and cracked status
            JsonObject accountObject = new JsonObject();
            accountObject.addProperty("Password", password);
            accountObject.addProperty("Cracked", cracked);

            // Get the existing accounts if any
            JsonObject accountsObject = rootObject.getAsJsonObject("Accounts");
            if (accountsObject == null) {
                accountsObject = new JsonObject();
            }

            // Add the account object to the accountsObject using the username as the key
            accountsObject.add(username, accountObject);

            // Add the updated accountsObject to the root object
            rootObject.add("Accounts", accountsObject);

            // Write the updated JSON to the file in pretty-printed format
            try (FileWriter writer = new FileWriter(accountsFile)) {
                writer.write(new GsonBuilder().setPrettyPrinting().create().toJson(rootObject));
            }

            return "Saved accounts.json";
        } catch (IOException e) {
            e.printStackTrace();
            return "Failed to save the accounts: " + e.getMessage();
        }
    }

    public static String addAccount(String username, String password, boolean cracked, String location) {
        return save(username, password, cracked, location);
    }

    public static String getAccounts(String location) {
        List<String> accountList = new ArrayList<>();

        try {
            // Create a File object for the accounts.json file
            File accountsFile = new File(location + "/accounts.json");

            if (accountsFile.exists()) {
                // Parse the JSON from the file
                JsonParser parser = new JsonParser();
                try (FileReader reader = new FileReader(accountsFile)) {
                    JsonElement rootElement = parser.parse(reader);
                    JsonObject rootObject = rootElement.getAsJsonObject();

                    // Get the "Accounts" object
                    JsonObject accountsObject = rootObject.getAsJsonObject("Accounts");

                    // Iterate over the accounts and add them to the list
                    for (Map.Entry<String, JsonElement> entry : accountsObject.entrySet()) {
                        String username = entry.getKey();
                        JsonObject accountObject = entry.getValue().getAsJsonObject();
                        String password = accountObject.get("Password").getAsString();
                        boolean cracked = accountObject.get("Cracked").getAsBoolean();

                        // Add the account to the list
                        accountList.add(username + ":" + (cracked ? "offline" : password));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Join the account list into a single string
        return String.join(",", accountList);
    }

    public static String deleteAccount(String username, String location) {
        try {
            File accountsFile = new File(location + "/accounts.json");

            if (accountsFile.exists()) {
                // Parse the JSON from the file
                JsonParser parser = new JsonParser();
                try (FileReader reader = new FileReader(accountsFile)) {
                    JsonElement rootElement = parser.parse(reader);
                    JsonObject rootObject = rootElement.getAsJsonObject();

                    // Get the "Accounts" object
                    JsonObject accountsObject = rootObject.getAsJsonObject("Accounts");

                    if (accountsObject != null && accountsObject.has(username)) {
                        // Remove the account with the specified username
                        accountsObject.remove(username);

                        // Write the updated JSON to the file in pretty-printed format
                        try (FileWriter writer = new FileWriter(accountsFile)) {
                            writer.write(new GsonBuilder().setPrettyPrinting().create().toJson(rootObject));
                        }

                        return "Deleted account: " + username;
                    } else {
                        return "Account not found: " + username;
                    }
                }
            } else {
                return "accounts.json file not found.";
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "Failed to delete the account: " + e.getMessage();
        }
    }

}
