package com.example.sbtv.data.model

import com.google.gson.annotations.SerializedName

data class XtreamSeries(
    @SerializedName("series_id") val seriesId: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("cover") val cover: String?,
    @SerializedName("category_id") val categoryId: String?
)
