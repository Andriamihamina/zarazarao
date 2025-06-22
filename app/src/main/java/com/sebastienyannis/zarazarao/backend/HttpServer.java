package com.sebastienyannis.zarazarao.backend;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;
import com.sebastienyannis.zarazarao.data.repository.ImageRepository;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import fi.iki.elonen.NanoWSD;


public class HttpServer extends NanoWSD {
    private final Context context;
    private static final String tag = "HttpServer";
    private boolean connected = false;

    public  HttpServer(Context context) {
        //TODO make dynamic
        super(8080);
        this.connected = true;
        this.context = context;
    }

    public boolean isConnected() {
        return connected;
    }

    private static class WSDSocket extends WebSocket {
        public WSDSocket(IHTTPSession handShakeRequest) {
            super (handShakeRequest);
        }

        @Override
        protected void onOpen() {
            System.out.println("WebSocket opened: " + this.getHandshakeRequest().getRemoteIpAddress());
        }

        @Override
        protected void onClose(WebSocketFrame.CloseCode code, String reason, boolean initiatedByRemote) {
            System.out.println("WebSocket closed: reason=" + reason + ", remote=" + initiatedByRemote);
        }

        @Override
        protected void onMessage(WebSocketFrame webSocketFrame) {
            try {
                send(webSocketFrame.toString() + " too");
            } catch (IOException e) {
                Log.e(HttpServer.tag, "could not send message", e);
            }
        }

        @Override
        protected void onPong(WebSocketFrame pong) {
            System.out.println("Pong received!");
        }

        @Override
        protected void onException(IOException exception) {
            System.err.println("WebSocket error: " + exception.getMessage());
            exception.printStackTrace();
        }
    }

    @Override
    protected WebSocket openWebSocket(IHTTPSession handshake) {
        return null;
    }

    @Override
    public Response serve(IHTTPSession session) {
        String uri = session.getUri();
        Response response;
        if (uri.equals("/")) {
            try {
                AssetManager assetManager = context.getAssets();
                InputStream inputStream = assetManager.open("web" + "/index.html");
                String mimeType = getMimeType("/index.html");
                response = newFixedLengthResponse(Response.Status.OK, mimeType, inputStream, inputStream.available());
            } catch (IOException e) {
                return newFixedLengthResponse(Response.Status.NOT_FOUND, MIME_PLAINTEXT, "404 Not Found");
            }
        }
        else if (uri.equals("/images")) {
            Map<String, List<String>> params = decodeParameters(session.getQueryParameterString());

            int offset = tryParseInt(params.get("offset"), 0);
            int limit = tryParseInt(params.get("limit"), 20);

            String json = ImageRepository.getInstance().paginateImages(offset, limit, context);
            response = newFixedLengthResponse(Response.Status.OK, "application/json", json);
        }
        else {
            try {
                AssetManager assetManager = context.getAssets();
                InputStream inputStream = assetManager.open("web" + uri);
                String mimeType = getMimeType(uri);
                response = newFixedLengthResponse(Response.Status.OK, mimeType, inputStream, inputStream.available());
                return response;
            } catch (IOException e) {
                return newFixedLengthResponse(Response.Status.NOT_FOUND, MIME_PLAINTEXT, "404 Not Found");
            }
        }
        response.addHeader("Access-Control-Allow-Origin", "*");
        return response;
    }

    private File resolveStaticFile(String uri) {
        String path = uri.equals("/") ? "/index.html" : uri;
        return new File("web" + path);
    }


    private int tryParseInt(List<String> values, int defaultValue) {
        if (values == null || values.isEmpty()) return defaultValue;
        try {
            return Integer.parseInt(values.get(0));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    //TODO move where appropriate
    public static class ImageInfo {
        public String uri;
        public  String thumbnail;

        public ImageInfo(String uri, String thumbnail) {
            this.uri = uri;
            this.thumbnail = thumbnail;
        }
    }

    public static class PaginatedImages {
        int total;
        int offset;
        int limit;
        List<ImageInfo> images;

        public PaginatedImages(int total, int offset, int limit, List<ImageInfo> images) {
            this.total = total;
            this.offset = offset;
            this.limit = limit;
            this.images = images;
        }
    }

    private Response serveImageList() {
        ImageRepository repository = ImageRepository.getInstance();
        List<Uri> imageUris = repository.loadImagesFromGallery(this.context);
        String json = repository.prepareJsonWithThumbnails(imageUris, this.context);

        return newFixedLengthResponse(Response.Status.OK, "application/json", json);

    }


    private String getMimeType(String uri) {
        if (uri.endsWith(".html")) return "text/html";
        if (uri.endsWith(".js")) return "application/javascript";
        if (uri.endsWith(".css")) return "text/css";
        if (uri.endsWith(".json")) return "application/json";
        if (uri.endsWith(".png")) return "image/png";
        if (uri.endsWith(".jpg") || uri.endsWith(".jpeg")) return "image/jpeg";
        if (uri.endsWith(".svg")) return "image/svg+xml";
        return "application/octet-stream";
    }

}
