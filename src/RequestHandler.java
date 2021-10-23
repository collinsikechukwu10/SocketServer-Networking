import enums.ResponseCode;
import exceptions.HttpException;
import exceptions.RequestNotSupportedException;
import exceptions.ResourceNotFoundException;

import java.io.*;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestHandler extends Thread {
    //    private InputStream is; // get data from client on this input stream
//    private OutputStream os; // can send data back to the client on this output stream
    private final static String PROTOCOL_METHOD = "HTTP/1.1";
    private final static List<String> ALLOWED_EXTENSIONS = List.of("png", "jpg", "pdf", "html", "csv");
    private final static LoggerService loggerService = new LoggerService(RequestHandler.class.getName());




    public boolean isAllowedFileExtensions(String filePath) {
        // this should check if the fille extension is right or not
        String[] splits = filePath.split("\\.");
        loggerService.info(splits[splits.length - 1]);
        return ALLOWED_EXTENSIONS.contains(splits[splits.length - 1].toLowerCase());
    }

    public Map<String, String> extractRequestHeaders(ArrayList<String> requestStringLines) {
        Map<String, String> headers = new HashMap<>();
        loggerService.info("====Request Received======");
        requestStringLines.forEach(line -> {
            if (line.contains(PROTOCOL_METHOD)) {
                String[] data = line.replace(PROTOCOL_METHOD, "").split("/");
                if (data.length == 2) {
                    headers.put(Headers.RequestMethod, data[0].trim());
                    headers.put(Headers.RelativeUrl, data[1].trim());
                }
            }
        });
        return headers;
    }


    public ArrayList<String> retrieveRequestString(BufferedReader br) throws IOException {
        ArrayList<String> lines = new ArrayList<>();
        String line;
        do {
            line = br.readLine();
            lines.add(line);
            loggerService.info(line);
        } while (!line.isEmpty());
        return lines;
    }

    public String extractRequestData() throws Exception {
        return "";
    }

    public File findFile(String basePath,  String relativePath){
        return Paths.get(basePath, relativePath).toFile();
    }
}
