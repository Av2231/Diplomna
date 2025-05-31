package com.example.pmu.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
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
import com.example.pmu.interfaces.UserCommentScoreListener;
import com.example.pmu.interfaces.UserRatingScoreListener;
import com.example.pmu.models.CommentModel;
import com.example.pmu.models.PinMarker;
import com.example.pmu.models.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RequestBuilder {

    static String ip = "192.168.0.159";
    static RequestQueue requestQueue;

    public RequestBuilder(Context context) {
        requestQueue = Volley.newRequestQueue(context);
    }


    public static void login(String email, String password, LoginAndRegisterListener listener) {
        String url = "http://" + ip + "/dbQueries/login.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("status");
                            String message = jsonObject.getString("message");
                            if (status.equals("success")) {
                                String email = jsonObject.getString("email");
                                String fName = jsonObject.getString("first_name");
                                String lName = jsonObject.getString("last_name");
                                String role = jsonObject.getString("role");
                                String dob = jsonObject.getString("dob");
                                String id = jsonObject.getString("id");
                                User.getInstance().setId(id);
                                User.getInstance().setEmail(email);
                                User.getInstance().setDob(dob);
                                User.getInstance().setLastName(lName);
                                User.getInstance().setFirstName(fName);
                                User.getInstance().setRole(role);
                                String img = jsonObject.getString("picture");
                                User.getInstance().setImage(img);
                                listener.onSuccess();
                            } else {
                                listener.onFailure(message);
                            }
                        } catch (JSONException ex) {
                            ex.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                listener.onErrorResponse("That didnt work !" + volleyError.getLocalizedMessage());
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("password", password);
                return params;
            }
        };
        requestQueue.add(stringRequest);

    }


    public static void register(User user, LoginAndRegisterListener listener) {
        String url = "http://" + ip + "/dbQueries/register.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("status");
                            String message = jsonObject.getString("message");
                            if (status.equals("success")) {
                                listener.onSuccess();
                            } else {
                                listener.onFailure(message);
                            }
                        } catch (JSONException ex) {
                            ex.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                listener.onErrorResponse("That didnt work !" + volleyError.getLocalizedMessage());
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("first_name", user.getFirstName());
                params.put("last_name", user.getLastName());
                params.put("email", user.getEmail());
                params.put("password", user.getPassword());
                params.put("isDeleted", user.getIsDeleted());
                params.put("isConfirmed", user.getIsConfirmed());
                params.put("role", user.getRole());
                params.put("dob", user.getDob());
                params.put("user_picture", user.getImage());
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    public static void getLocationsFromAPI(String type, String city, String region, DataParserListener listner) {
        String gisUrl = "https://geocode.arcgis.com/arcgis/rest/services/World/GeocodeServer/findAddressCandidates" +
                "?SingleLine=" + type + "%20" + city + "%20" + region + "&outFields=type,city,region&maxLocations=20&forStorage=false&f=pjson";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, gisUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject responseObject = new JSONObject(response);
                            JSONArray candidatesArray = responseObject.getJSONArray("candidates");

                            ArrayList<PinMarker> receivedData = new ArrayList<>();

                            for (int i = 0; i < candidatesArray.length(); i++) {
                                JSONObject candidateObject = candidatesArray.getJSONObject(i);
                                PinMarker marker = new PinMarker();
                                marker.setX(Double.parseDouble(candidateObject.getJSONObject("location").getString("x")));
                                marker.setY(Double.parseDouble(candidateObject.getJSONObject("location").getString("y")));
                                marker.setTitle(candidateObject.getString("address"));
                                marker.setLocation(candidateObject.getJSONObject("attributes").getString("city"));
                                marker.setType(candidateObject.getJSONObject("attributes").getString("type"));
                                marker.setId();
                                receivedData.add(marker);
                            }
                            listner.onSuccess(receivedData);
                        } catch (JSONException ex) {
                            ex.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                String error = volleyError.getLocalizedMessage();
                listner.onFailure(error);
            }
        });
        requestQueue.add(stringRequest);
    }

    public static void getAllComments(String locationId, CommentsListener listener) {
        String url = "http://" + ip + "/dbQueries/select_comments.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject responseObject = new JSONObject(response);
                            JSONArray commentsJson = responseObject.getJSONArray("comments");
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
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                String error = volleyError.getLocalizedMessage();
                listener.onFailure(error);
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("location_id", String.valueOf(locationId));
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    public static void addNewComment(String locationId, String userId, String comment, NewCommentListener listener) {
        String url = "http://" + ip + "/dbQueries/insert_comment.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("status");
                            String message = jsonObject.getString("message");
                            if (status.equals("success")) {
                                listener.onSuccess();
                            } else {
                                listener.onFailure(message);
                            }
                        } catch (JSONException ex) {
                            ex.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                listener.onFailure("That didnt work !" + volleyError.getLocalizedMessage());
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", userId);
                params.put("location_id", locationId);
                params.put("comment", comment);
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    public static void rateLocation(String locationId, String userId, String userRate, RateLocationListener listener) {
        String url = "http://" + ip + "/dbQueries/insert_rating.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("status");
                            String message = jsonObject.getString("message");
                            if (status.equals("success")) {
                                listener.onSuccess();
                            } else {
                                listener.onFailure(message);
                            }
                        } catch (JSONException ex) {
                            ex.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                listener.onFailure("That didnt work !" + volleyError.getLocalizedMessage());
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", userId);
                params.put("location_id", locationId);
                params.put("rated", userRate);
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    public static void addNewLocation(String locationId, NewLocationListener listener) {
        String url = "http://" + ip + "/dbQueries/insert_locations.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("status");
                            String message = jsonObject.getString("message");
                            if (status.equals("success")) {
                                listener.onSuccess(jsonObject.getString("generated_location_id"));
                            } else {
                                listener.onFailure(message);
                            }
                        } catch (JSONException ex) {
                            ex.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                listener.onFailure("That didnt work !" + volleyError.getLocalizedMessage());
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("location_id", locationId);
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    public static void checkIfUserRatedLocation(String locationId, String userId, AlreadyRatedLocationListener listener) {
        String url = "http://" + ip + "/dbQueries/select_user_from_rating.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("status");
                            String message = jsonObject.getString("message");
                            if (status.equals("success")) {
                                listener.onSuccess(jsonObject.getString("rated"));
                            } else {
                                listener.onFailure(message);
                            }
                        } catch (JSONException ex) {
                            ex.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

                listener.onFailure("That didnt work !" + volleyError.getLocalizedMessage());
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", userId);
                params.put("location_id", locationId);
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    public static void checkIfLocationIsExisting(String locationId, NewLocationListener listener) {

        String url = "http://" + ip + "/dbQueries/select_locations.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("status");
                            String message = jsonObject.getString("message");
                            if (status.equals("success")) {
                                listener.onSuccess(jsonObject.getString("base_id"));
                            } else {
                                listener.onFailure(message);
                            }
                        } catch (JSONException ex) {
                            ex.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

                listener.onFailure("That didnt work !" + volleyError.getLocalizedMessage());
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("location_id", locationId);
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    public static void getLocationRating(String locationId, LocationRatingListener listener) {
        String url = "http://" + ip + "/dbQueries/select_rating.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("status");
                            String message = jsonObject.getString("message");
                            if (status.equals("success")) {
                                listener.onSuccess(jsonObject.getString("average_rating"));
                            } else if (status.equals("not rated")) {
                                listener.onSuccess("0");
                            } else {
                                listener.onFailure(message);
                            }
                        } catch (JSONException ex) {
                            ex.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                listener.onFailure("That didnt work !" + volleyError.getLocalizedMessage());
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("location_id", locationId);
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    public static void getUserRatingScore(String userId, UserRatingScoreListener listener) {
        String url = "http://" + ip + "/dbQueries/select_rated_locations_by_user.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("status");
                            String message = jsonObject.getString("message");
                            if (status.equals("success")) {
                                listener.onSuccess(jsonObject.getString("average_rating"), jsonObject.getString("rate_score"));
                            } else {
                                listener.onFailure(message);
                            }
                        } catch (JSONException ex) {
                            ex.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                String error = volleyError.getLocalizedMessage();
                listener.onFailure(error);
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", userId);
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    public static void getUserCommentScore(String userId, UserCommentScoreListener listener) {
        String url = "http://" + ip + "/dbQueries/select_number_of_comments.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("status");
                            String message = jsonObject.getString("message");
                            if (status.equals("success")) {
                                listener.onSuccess(jsonObject.getString("comment_score"));
                            } else {
                                listener.onFailure(message);
                            }
                        } catch (JSONException ex) {
                            ex.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                String error = volleyError.getLocalizedMessage();
                listener.onFailure(error);
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", userId);
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    public static void newProfilePicture(String newPicture, String userId, AddNewProfilePictureListener listener) {
        String url = "http://" + ip + "/dbQueries/change_picture.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("status");
                            String message = jsonObject.getString("message");
                            if (status.equals("success")) {
                                listener.onSuccess();
                            } else {
                                listener.onFailure(message);
                            }
                        } catch (JSONException ex) {
                            ex.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                String error = volleyError.getLocalizedMessage();
                listener.onFailure(error);
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_picture", newPicture);
                params.put("user_id", userId);
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }
}