package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.MemAuthDAO;
import dataaccess.MemGameDAO;
import dataaccess.MemUserDOA;
import server.requests.RegisterRequest;
import server.responses.RegisterResponse;
import service.ClearService;
import service.RegisterService;
import spark.*;

public class Server {

    private RegisterService registerService;
    private ClearService clearService;
    private final Gson gson = new Gson();

    public int run(int desiredPort) {

        MemUserDOA userDAO = new MemUserDOA();
        MemAuthDAO authDAO = new MemAuthDAO();
        MemGameDAO gameDAO = new MemGameDAO();

        registerService = new RegisterService(userDAO, authDAO);
        clearService = new ClearService(gameDAO, authDAO, userDAO);

        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.post("/user", this::registerHandler);
        Spark.delete("/db", this::clearHandler);

        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private Object clearHandler(Request request, Response response) {
        try {
            clearService.clear();

            response.status(200);
            response.body("");
            return "";
        } catch (DataAccessException error) {
            response.status(403);
            response.body("{\"message\": \"Error: no object found\"}");
            return "{\"message\": \"Error: no object found\"}";
        }
    }

    private Object registerHandler(Request req, Response res) {
        try {
            RegisterRequest regReq = gson.fromJson(req.body(), RegisterRequest.class);

            String authToken = registerService.createUser(regReq.getUsername(), regReq.getPassword(), regReq.getEmail());

            RegisterResponse regRes = new RegisterResponse(regReq.getUsername(), authToken);

            res.type("application/json");

            return gson.toJson(regRes);
        }
        catch (DataAccessException error) {
            res.status(403);
            res.body("{\"message\": \"Error: username already in use\"}");
            return "{\"message\": \"Error: username already in use\"}";
        }
        catch (IllegalArgumentException error) {
            res.status(400);
            res.body("{\"message\": \"Error: Invalid username, email, or password\"}");
            return "{\"message\": \"Error: Invalid username, email, or password\"}";
        }
    }
}
