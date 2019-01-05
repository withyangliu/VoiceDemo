package com.withyang.voicedemo.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by withyang on 2018/3/26 0026.
 */

public class UnderstanderResultUtil {
    /**
     * 云知声结果获取
     *
     * @param jsonResult
     * @return
     */
    public static String asrResultOperate(String jsonResult) {
        StringBuffer mAsrResultBuffer = new StringBuffer();
        JSONObject asrJson;
        try {
            asrJson = new JSONObject(jsonResult);
            JSONArray asrJsonArray = asrJson.getJSONArray("net_asr");
            JSONObject asrJsonObject = asrJsonArray.getJSONObject(0);
            String asrJsonStatus = asrJsonObject.getString("result_type");
            if (asrJsonStatus.equals("change")) {
            } else {
                mAsrResultBuffer.append(asrJsonObject.getString("recognition_result"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return mAsrResultBuffer.toString();
    }
}
