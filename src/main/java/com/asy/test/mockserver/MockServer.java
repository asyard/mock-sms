package com.asy.test.mockserver;

import org.mockserver.integration.ClientAndServer;
import org.mockserver.mock.action.ExpectationCallback;
import org.mockserver.model.*;

import java.util.List;

import static org.mockserver.model.HttpClassCallback.callback;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

public class MockServer {

    private static int DEFAULT_PORT = 8055;

    public static void main(String[] args) {
        /*ClientAndServer clientAndServer = ClientAndServer.startClientAndServer(new Integer[] { Integer.valueOf(8055)});
        clientAndServer.when(HttpRequest.request().withMethod("GET"))
                .respond(HttpResponse.response().withStatusCode(Integer.valueOf(200))
                        .withHeaders(new Header[] {
                                new Header("Content-Type", new String[] {"application-json; charset=UTF-8"}),
                                new Header("Cache-Control", new String[] {"public, max-age=86400"})
                        })
                );*/

        int port = DEFAULT_PORT;

        if (args != null && args.length > 0) {
            port = Integer.parseInt(args[0]);
        }

        new ClientAndServer(port)
                .when(
                        request()
                                .withMethod("GET")
                )
                .callback(
                        callback()
                                .withCallbackClass("com.asy.test.mockserver.MockServer$CustomExpectationCallback")
                );

        System.out.println("Listening on port : " + port);

    }


    public static class CustomExpectationCallback implements ExpectationCallback {

        @Override
        public HttpResponse handle(HttpRequest httpRequest) {
            try {
                List<Parameter> entries = httpRequest.getQueryStringParameters().getEntries();
                if(entries.size() != 4) {
                    System.out.println("Parameter count is not valid. Expected : 4, received : " + entries.size());
                    return response().withStatusCode(HttpStatusCode.BAD_REQUEST_400.code()).withBody("invalid_request");
                }

                String username = String.valueOf(entries.get(0).getValues().get(0));
                String pass = String.valueOf(entries.get(1).getValues().get(0));
                String to = String.valueOf(entries.get(2).getValues().get(0));
                String msg = String.valueOf(entries.get(3).getValues().get(0));

                System.out.println("Received : " + username + ", " + pass + ", " + to + ", " + msg);

                if (!"test".equals(username) || !"test".equals(pass)) {
                    return response().withStatusCode(HttpStatusCode.UNAUTHORIZED_401.code()).withBody("authentication_failed");
                }


                return response()
                        .withStatusCode(HttpStatusCode.OK_200.code())
                        /*.withHeaders(
                                header("x-callback", "test_callback_header"),
                                header("Content-Length", "a_callback_response".getBytes("UTF-8").length)
                                header("Connection", "keep-alive")
                        )*/
                        .withBody("OK");


            } catch (Exception e) {
                e.printStackTrace();
                return response().withStatusCode(HttpStatusCode.BAD_REQUEST_400.code()).withBody("parameter_mismatch");
            }



            /*if (httpRequest.getPath().getValue().endsWith("/path")) {
                try {
                    return response()
                            .withStatusCode(HttpStatusCode.OK_200.code())
                            .withHeaders(
                                    header("x-callback", "test_callback_header"),
                                    header("Content-Length", "a_callback_response".getBytes("UTF-8").length),
                                    header("Connection", "keep-alive")
                            )
                            .withBody("OK");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    return response().withStatusCode(HttpStatusCode.INTERNAL_SERVER_ERROR_500.code()).withBody("hata");
                }
            } else {
                return notFoundResponse();
            }*/
        }
    }



    /*

    new MockServerClient("localhost", 1080)
    .when(
        request()
            .withMethod("POST")
            .withPath("/login")
            .withBody("{username: 'foo', password: 'bar'}")
    )
    .respond(
        response()
            .withStatusCode(302)
            .withCookie(
                "sessionId", "2By8LOhBmaW5nZXJwcmludCIlMDAzMW"
            )
            .withHeader(
                "Location", "https://www.mock-server.com"
            )
    );

     */


}
