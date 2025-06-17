package com.example.pmu.utils;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.pmu.interfaces.AddNewProfilePictureListener;
import com.example.pmu.interfaces.AlreadyRatedLocationListener;
import com.example.pmu.interfaces.CommentsListener;
import com.example.pmu.interfaces.DataParserListener;
import com.example.pmu.interfaces.LocationRatingListener;
import com.example.pmu.interfaces.LoginAndRegisterListener;
import com.example.pmu.interfaces.NewCommentListener;
import com.example.pmu.interfaces.NewLocationListener;
import com.example.pmu.interfaces.RateLocationListener;
import com.example.pmu.interfaces.ReservationsListener;
import com.example.pmu.interfaces.UserCommentScoreListener;
import com.example.pmu.interfaces.UserRatingScoreListener;
import com.example.pmu.models.CommentModel;
import com.example.pmu.models.LocationModel;
import com.example.pmu.models.PinMarker;
import com.example.pmu.models.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ServerCommunication {

    static String ip = "192.168.0.159:8080";
    static RequestQueue requestQueue;

    public ServerCommunication(Context context) {
        requestQueue = Volley.newRequestQueue(context);
    }


    public static void login(String email, String password, LoginAndRegisterListener listener) {
        String url = "http://" + ip + "/api/login";

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("email", email);
            jsonBody.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        JsonObjectRequest jsonRequest = new JsonObjectRequest(
                Request.Method.POST, url, jsonBody, response -> {
                    try {
                        String status = response.getString("status");
                        String message = response.getString("message");
                        if (status.equals("success")) {
                            String userId = response.getString("id");
                            String emailVal = response.getString("email");
                            String firstName = response.getString("first_name");
                            String lastName = response.getString("last_name");
                            String role = response.getString("role");
                            String dob = response.optString("dob", null);
                            String picture = response.optString("picture", "");

                            User.getInstance().setId(userId);
                            User.getInstance().setEmail(emailVal);
                            User.getInstance().setFirstName(firstName);
                            User.getInstance().setLastName(lastName);
                            User.getInstance().setRole(role);
                            User.getInstance().setDob(dob);
                            User.getInstance().setImage(picture);

                            listener.onSuccess();
                        } else {
                            listener.onFailure(message);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        listener.onFailure("Invalid response format.");
                    }
                },

                error -> {
                    Log.e("VolleyError", "Request failed", error);
                    listener.onErrorResponse("That didn't work! " + error.getLocalizedMessage());
                }

        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        requestQueue.add(jsonRequest);
    }


    public static void register(User user, LoginAndRegisterListener listener) {
        String url = "http://" + ip + "/api/register";

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("first_name", user.getFirstName());
            jsonBody.put("last_name", user.getLastName());
            jsonBody.put("email", user.getEmail());
            jsonBody.put("password", user.getPassword());
            jsonBody.put("isDeleted", user.getIsDeleted());
            jsonBody.put("isConfirmed", user.getIsConfirmed());
            jsonBody.put("role", user.getRole());
            jsonBody.put("dob", user.getDob());
            jsonBody.put("user_picture", user.getImage());
        } catch (JSONException e) {
            e.printStackTrace();
            listener.onErrorResponse("Invalid input data.");
            return;
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                response -> {
                    try {
                        String status = response.getString("status");
                        String message = response.getString("message");
                        if ("success".equals(status)) {
                            listener.onSuccess();
                        } else {
                            listener.onFailure(message);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        listener.onErrorResponse("Response parsing error.");
                    }
                },
                error -> listener.onErrorResponse("That didn't work! " + error.getLocalizedMessage())

        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        requestQueue.add(jsonObjectRequest);
    }

    public static void getUserReservations(String userId, ReservationsListener listener) {
        String url = "http://" + ip + "/api/get_user_reservations";

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("user_id", userId);
        } catch (JSONException e) {
            e.printStackTrace();
            listener.onFailure("Failed to build request");
            return;
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST, url, jsonBody, response -> {
                    try {
                        String status = response.getString("status");
                        if (status.equals("success")) {
                            JSONArray reservationsJson = response.getJSONArray("reservations");
                            ArrayList<LocationModel> reservations = new ArrayList<>();
                            for (int i = 0; i < reservationsJson.length(); i++) {
                                JSONObject res = reservationsJson.getJSONObject(i);
                                LocationModel reservation = new LocationModel();
                                reservation.setPlace(res.getString("location_name"));
                                reservation.setCategory(res.getString("category"));
                                String fromDateStr = res.getString("date_from");
                                String toDateStr = res.getString("date_to");
                                reservation.setFromDate(fromDateStr);
                                reservation.setToDate(toDateStr);
                                reservations.add(reservation);
                            }
                            listener.onSuccess(reservations);
                        } else {
                            listener.onFailure(response.getString("message"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        listener.onFailure("Failed to parse response");
                    }
                },
                error -> {
                    error.printStackTrace();
                    listener.onFailure("Request failed: " + error.getLocalizedMessage());
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        requestQueue.add(jsonObjectRequest);
    }

    public static void findLocations(String category, String region, String fromDate, String toDate, DataParserListener listener) {
        String url = "http://" + ip + "/api/find_locations";

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("category", category);
            jsonBody.put("region", region);
            jsonBody.put("fromDate", fromDate);
            jsonBody.put("toDate", toDate);
        } catch (JSONException e) {
            e.printStackTrace();
            listener.onFailure("Invalid input data.");
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                response -> {
                    try {
                        JSONArray results = response.getJSONArray("locations");
                        ArrayList<PinMarker> locations = new ArrayList<>();

                        for (int i = 0; i < results.length(); i++) {
                            JSONObject loc = results.getJSONObject(i);
                            PinMarker marker = new PinMarker();
                            marker.setId();
                            marker.setTitle(loc.getString("address"));
                            marker.setType(loc.getString("category"));
                            marker.setLocation(loc.getString("region"));
                            marker.setX(loc.getDouble("longitude"));
                            marker.setY(loc.getDouble("latitude"));
                            locations.add(marker);
                        }

                        listener.onSuccess(locations);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        listener.onFailure("Parsing error");
                    }
                },
                error -> listener.onFailure("Request failed: " + error.getLocalizedMessage())
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        requestQueue.add(request);
    }

    public static void getAllComments(String locationId, CommentsListener listener) {
        String url = "http://" + ip + "/api/select_comments";

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("location_id", locationId);
        } catch (JSONException e) {
            e.printStackTrace();
            listener.onFailure("Invalid request data.");
            return;
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST, url, jsonBody, response -> {
                    try {
                        JSONArray commentsJson = response.getJSONArray("comments");
                        ArrayList<CommentModel> result = new ArrayList<>();
                        for (int i = 0; i < commentsJson.length(); i++) {
                            JSONObject commentsJsonJSONObject = commentsJson.getJSONObject(i);
                            CommentModel commentModel = new CommentModel();
                            commentModel.setUser(String.format("%s %s", commentsJsonJSONObject.getString("user_first_name"), commentsJsonJSONObject.getString("user_last_name")));
                            commentModel.setComment(commentsJsonJSONObject.getString("comment"));
                            commentModel.setProfilePic(commentsJsonJSONObject.getString("user_picture"));
                            result.add(commentModel);
                        }
                        listener.onSuccess(result);
                    } catch (JSONException ex) {
                        ex.printStackTrace();
                        listener.onFailure("Error parsing response.");
                    }
                },
                error -> {
                    error.printStackTrace();
                    listener.onFailure("Request failed: " + error.getLocalizedMessage());
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        requestQueue.add(jsonObjectRequest);
    }

    public static void addNewComment(String locationId, String userId, String comment, NewCommentListener listener) {
        String url = "http://" + ip + "/api/insert_comment";

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("user_id", userId);
            jsonBody.put("location_id", locationId);
            jsonBody.put("comment", comment);
        } catch (JSONException e) {
            e.printStackTrace();
            listener.onFailure("Invalid input data.");
            return;
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST, url, jsonBody, response -> {
                    try {
                        String status = response.getString("status");
                        String message = response.getString("message");
                        if ("success".equals(status)) {
                            listener.onSuccess();
                        } else {
                            listener.onFailure(message);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        listener.onFailure("Response parsing error.");
                    }
                },
                error -> {
                    error.printStackTrace();
                    listener.onFailure("That didn't work! " + error.getLocalizedMessage());
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        requestQueue.add(jsonObjectRequest);
    }

    public static void rateLocation(String locationId, String userId, String userRate, RateLocationListener listener) {
        String url = "http://" + ip + "/api/insert_rating";

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("user_id", userId);
            jsonBody.put("location_id", locationId);
            jsonBody.put("rated", userRate);
        } catch (JSONException e) {
            e.printStackTrace();
            listener.onFailure("Invalid input data.");
            return;
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST, url, jsonBody, response -> {
                    try {
                        String status = response.getString("status");
                        String message = response.getString("message");
                        if ("success".equals(status)) {
                            listener.onSuccess();
                        } else {
                            listener.onFailure(message);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        listener.onFailure("Response parsing error.");
                    }
                },
                error -> {
                    error.printStackTrace();
                    listener.onFailure("That didn't work! " + error.getLocalizedMessage());
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        requestQueue.add(jsonObjectRequest);
    }

    public static void addNewLocation(String locationId, NewLocationListener listener) {
        String url = "http://" + ip + "/api/insert_locations";

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("location_id", locationId);
        } catch (JSONException e) {
            e.printStackTrace();
            listener.onFailure("Invalid input data.");
            return;
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST, url, jsonBody, response -> {
                    try {
                        String status = response.getString("status");
                        String message = response.getString("message");
                        if ("success".equals(status)) {
                            listener.onSuccess(response.getString("generated_location_id"));
                        } else {
                            listener.onFailure(message);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        listener.onFailure("Response parsing error.");
                    }
                },
                error -> {
                    error.printStackTrace();
                    listener.onFailure("Request failed: " + error.getLocalizedMessage());
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        requestQueue.add(jsonObjectRequest);
    }

    public static void checkIfUserRatedLocation(String locationId, String userId, AlreadyRatedLocationListener listener) {
        String url = "http://" + ip + "/api/select_user_from_rating";

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("user_id", userId);
            jsonBody.put("location_id", locationId);
        } catch (JSONException e) {
            e.printStackTrace();
            listener.onFailure("Invalid input data.");
            return;
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST, url, jsonBody, response -> {
                    try {
                        String status = response.getString("status");
                        String message = response.getString("message");
                        if ("success".equals(status)) {
                            listener.onSuccess(response.getString("rated"));
                        } else {
                            listener.onFailure(message);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        listener.onFailure("Response parsing error.");
                    }
                },
                error -> {
                    error.printStackTrace();
                    listener.onFailure("Request failed: " + error.getLocalizedMessage());
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        requestQueue.add(jsonObjectRequest);
    }

    public static void checkIfLocationIsExisting(String locationId, NewLocationListener listener) {
        String url = "http://" + ip + "/api/select_locations";

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("location_id", locationId);
        } catch (JSONException e) {
            e.printStackTrace();
            listener.onFailure("Invalid input data.");
            return;
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST, url, jsonBody, response -> {
                    try {
                        String status = response.getString("status");
                        String message = response.getString("message");
                        if ("success".equals(status)) {
                            listener.onSuccess(response.getString("base_id"));
                        } else {
                            listener.onFailure(message);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        listener.onFailure("Response parsing error.");
                    }
                },
                error -> {
                    error.printStackTrace();
                    listener.onFailure("Request failed: " + error.getLocalizedMessage());
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        requestQueue.add(jsonObjectRequest);
    }

    public static void getLocationRating(String locationId, LocationRatingListener listener) {
        String url = "http://" + ip + "/api/select_rating";

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("location_id", locationId);
        } catch (JSONException e) {
            e.printStackTrace();
            listener.onFailure("Invalid input data.");
            return;
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST, url, jsonBody, response -> {
                    try {
                        String status = response.getString("status");
                        String message = response.getString("message");
                        if ("success".equals(status)) {
                            listener.onSuccess(response.getString("average_rating"));
                        } else if ("not rated".equals(status)) {
                            listener.onSuccess("0");
                        } else {
                            listener.onFailure(message);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        listener.onFailure("Response parsing error.");
                    }
                },
                error -> {
                    error.printStackTrace();
                    listener.onFailure("Request failed: " + error.getLocalizedMessage());
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        requestQueue.add(jsonObjectRequest);
    }

    public static void getUserRatingScore(String userId, UserRatingScoreListener listener) {
        String url = "http://" + ip + "/api/select_rated_locations_by_user";

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("user_id", userId);
        } catch (JSONException e) {
            e.printStackTrace();
            listener.onFailure("Invalid input data.");
            return;
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST, url, jsonBody, response -> {
                    try {
                        String status = response.getString("status");
                        String message = response.getString("message");
                        if ("success".equals(status)) {
                            String avgRating = response.getString("average_rating");
                            String rateScore = response.getString("rate_score");
                            listener.onSuccess(avgRating, rateScore);
                        } else {
                            listener.onFailure(message);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        listener.onFailure("Response parsing error.");
                    }
                },
                error -> {
                    error.printStackTrace();
                    listener.onFailure("Request failed: " + error.getLocalizedMessage());
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        requestQueue.add(jsonObjectRequest);
    }

    public static void getUserCommentScore(String userId, UserCommentScoreListener listener) {
        String url = "http://" + ip + "/api/select_number_of_comments";

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("user_id", userId);
        } catch (JSONException e) {
            e.printStackTrace();
            listener.onFailure("Invalid input data.");
            return;
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST, url, jsonBody, response -> {
                    try {
                        String status = response.getString("status");
                        String message = response.getString("message");
                        if ("success".equals(status)) {
                            listener.onSuccess(response.getString("comment_score"));
                        } else {
                            listener.onFailure(message);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        listener.onFailure("Response parsing error.");
                    }
                },
                error -> {
                    error.printStackTrace();
                    listener.onFailure("Request failed: " + error.getLocalizedMessage());
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        requestQueue.add(jsonObjectRequest);
    }

    public static void newProfilePicture(String newPicture, String userId, AddNewProfilePictureListener listener) {
        String url = "http://" + ip + "/api/change_picture";

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("user_picture", newPicture);
            jsonBody.put("user_id", userId);
        } catch (JSONException e) {
            e.printStackTrace();
            listener.onFailure("Invalid input data.");
            return;
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST, url, jsonBody, response -> {
                    try {
                        String status = response.getString("status");
                        String message = response.getString("message");
                        if ("success".equals(status)) {
                            listener.onSuccess();
                        } else {
                            listener.onFailure(message);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        listener.onFailure("Response parsing error.");
                    }
                },
                error -> {
                    error.printStackTrace();
                    listener.onFailure("That didn't work! " + error.getLocalizedMessage());
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        requestQueue.add(jsonObjectRequest);
    }
}