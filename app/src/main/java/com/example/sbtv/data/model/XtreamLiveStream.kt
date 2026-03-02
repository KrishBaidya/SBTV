package com.example.sbtv.data.model

import com.google.gson.annotations.SerializedName

data class XtreamLiveStream(
    @SerializedName("name") val name: String?,
    @SerializedName("stream_id") val streamId: String?,
    @SerializedName("stream_icon") val streamIcon: String?,
    @SerializedName("category_id") val categoryId: String?
)
