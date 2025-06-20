package com.sebastienyannis.zarazarao.backend;

import static android.text.Html.escapeHtml;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

public class HttpServer extends NanoHTTPD {

    public HttpServer() throws IOException {
        super(8080);
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
    }

    @Override
    public Response serve(IHTTPSession session) {
        String msg = "<html><body><h1>Hello Server </h1>\n";
        Map<String, List<String>> params = session.getParameters();
        List<String> username = params.get("username");
        if (username == null || username.isEmpty() || username.get(0).isEmpty()) {
            msg += "<form action='?' method='get'>\n" +
                    "<p>Your name: <input type='text' name='username'></p>\n" +
                    "<input type='submit' value='Submit'>\n" +
                    "</form>\n";
        }
        else {
            msg += "<p>Hello, " + escapeHtml(params.get("username").get(0)) + "!</p>";
        }
        return newFixedLengthResponse(msg + "</body></html>\n");
    }

}
