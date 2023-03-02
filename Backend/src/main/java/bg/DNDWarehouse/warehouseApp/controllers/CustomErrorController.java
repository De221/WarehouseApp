package bg.DNDWarehouse.warehouseApp.controllers;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

@RestController
public class CustomErrorController implements ErrorController {
    @RequestMapping("/error")
    public String handleError(HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if(status != null) {
            Integer statusCode = Integer.valueOf(status.toString());

            if(statusCode == HttpStatus.NOT_FOUND.value())
            {
                return "Incorrect URL!";
            }
            else if (statusCode == HttpStatus.METHOD_NOT_ALLOWED.value()) {
                return "Wrong HTTP request type!";
            }
            else if (statusCode == HttpStatus.BAD_REQUEST.value()) {
                return "Incorrect HTTP request params!";
            }
            else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                return "Querry handling went wrong, search for help from administrator!";
            }
        }
        return "Unknown error type! Search for help from administrator!";
    }
}
