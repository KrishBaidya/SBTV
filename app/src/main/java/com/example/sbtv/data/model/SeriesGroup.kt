package com.example.sbtv.data.model

/**
 * Represents a deduplicated series "show" with all its episodes grouped together.
 * Used in the UI to show one card per series, with episodes accessible on click.
 *
 * For Xtream providers, [xtreamSeriesId] is set so episodes can be fetched on-demand
 * via `get_series_info` when the user clicks the series.
 */
data class SeriesGroup(
    val id: String,
    val name: String,
    val poster: String? = null,
    val category: String? = null,
    val episodes: List<Series> = emptyList(),
    val xtreamSeriesId: String? = null,
    val episodesLoaded: Boolean = false
) {
    /** All distinct seasons found in this series' episodes */
    val seasons: List<String>
        get() = episodes.mapNotNull { it.season }.distinct().sorted()
    
    /** Get episodes filtered by season */
    fun episodesForSeason(season: String): List<Series> =
        episodes.filter { it.season == season }
}

