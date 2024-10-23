package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.MemUserDOA;
import server.requests.RegisterRequest;
import server.responses.RegisterResponse;
import service.RegisterService;
import spark.*;

public class Server {

    private RegisterService registerService;
    private final Gson gson = new Gson();

    public int run(int desiredPort) {

        MemUserDOA userDAO = new MemUserDOA();
        registerService = new RegisterService(userDAO, null);

        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.get("/test", (Request req, Response res)->{
            res.body("Hello");
            return "hello";
        });
        Spark.post("/user", this::registerHandler);

        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
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
            res.status(4033);
            res.body("{\"message\": \"Error: username already in use\"}");
            return "{\"message\": \"Error: username already in use\"}";
        }
    }
}
