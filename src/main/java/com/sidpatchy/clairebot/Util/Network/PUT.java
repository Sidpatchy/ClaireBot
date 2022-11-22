package com.sidpatchy.clairebot.Util.Network;

import com.sidpatchy.clairebot.Main;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class PUT {
    public String putToURL(String link, String json) throws IOException {
        URL url = new URL(link);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        Main.getLogger().info(link);

        // Authentication
        String userpass = Main.getApiUser() + ":" + Main.getApiPassword();
        String basicAuth = "Basic " + new String(Base64.getEncoder().encode(userpass.getBytes()));
        conn.setRequestProperty("Authorization", basicAuth);

        // PUT
        conn.setDoOutput( true );
        conn.setInstanceFollowRedirects( false );
        conn.setRequestMethod("PUT");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "application/json");
        conn.setUseCaches( false );
        try( OutputStream os = new DataOutputStream( conn.getOutputStream())) {
            byte[] input = json.getBytes(StandardCharsets.UTF_8);
            os.write( input, 0, input.length );
        }

        Main.getLogger().info(json);

        // Read response
        try(BufferedReader br = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String responseLine = null;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }

            return response.toString();
        }
    }
}
