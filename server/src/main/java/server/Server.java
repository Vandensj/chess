package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.MemAuthDAO;
import dataaccess.MemGameDAO;
import dataaccess.MemUserDOA;
import org.eclipse.jetty.security.LoginService;
import server.requests.LoginRequest;
import server.requests.RegisterRequest;
import server.responses.LoginResponse;
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
        loginService = new LoginService(userDAO, authDAO);

        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.post("/user", this::registerHandler);
        Spark.delete("/db", this::clearHandler);
        Spark.post("/session", this::loginHandler);

        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    private Object loginHandler(Request request, Response response) {
        try {
            LoginRequest loginRequest = gson.fromJson(request.body(), LoginRequest.class);

            String authToken = loginService.loginUser(loginRequest.getUsername(), loginRequest.getPassword());
            LoginResponse loginResponse = new LoginResponse(loginRequest.getUsername(), authToken);

            response.type("application/json");
            response.status(200);

            return gson.toJson(loginResponse);
        } catch (DataAccessException error) {
            response.status(401);
            response.body("{\"message\": \"Error: unauthorized\"}");
        }
    }

    private Object clearHandler(Request request, Response response) {
        try {
            clearService.clear();

            response.status(200);
            response.body("");
            return "";
        } catch (DataAccessException error) {
            response.status(500);
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
            res.status(200);

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

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
