package com.example.yownner;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface GetDataService {
    @FormUrlEncoded
    @POST("get_memo.php")
    Call<List<Item_memo>> getItems(@Field("date") String selectedDate,@Field("year") String year,@Field("month") String month,@Field("day") String day, @Field("pid") String pid);

    @FormUrlEncoded
    @POST("get_memo.php")
    Call<List<Item_memo>> getItemList(@Field("year") String year, @Field("month") String month, @Field("Complet") String Complet, @Field("pid") String pid);

    @FormUrlEncoded
    @POST("get_memo.php")
    Call<List<Item_memo>> getItemLists(@Field("pid") String pid,@Field("date") String selectedDate);

    @FormUrlEncoded
    @POST("get_record_timelist.php")
    Call<List<Item_time>> getItemTime( @Field("upid") String upid);

    @FormUrlEncoded
    @POST("get_timerList2.php")
    Call<List<Item_timer>> getItemAllTimer( @Field("upid") String upid);

    // 이 메서드는 서버로 PID를 보내기 위해 추가합니다.
    @POST("check_memo.php") // 서버의 API 엔드포인트에 맞게 변경하세요.
    @FormUrlEncoded
    Call<Void> sendPidToServer(@Field("pid") String pid,@Field("isChecked") boolean isChecked);
}

