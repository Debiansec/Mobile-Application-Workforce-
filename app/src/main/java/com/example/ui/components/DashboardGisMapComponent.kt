package com.example.ui.components

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Wc
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.ui.theme.ComplianceAmber
import com.example.ui.theme.ComplianceGreen
import com.example.ui.theme.WorkforceCardBorder
import com.example.ui.theme.WorkforceDarkBg
import com.example.ui.theme.WorkforceOrange
import com.example.ui.theme.WorkforceRed
import com.example.ui.theme.WorkforceSurface
import com.example.ui.theme.WorkforceSurfaceVariant
import com.example.ui.theme.WorkforceTextMuted
import com.example.ui.theme.WorkforceTextPrimary
import com.example.ui.theme.WorkforceTextSecondary

data class MonitoredMiningSiteGis(
    val id: String,
    val name: String,
    val company: String,
    val region: String,
    val latitude: Double,
    val longitude: Double,
    val cadastralProperty: String,
    val miningRightCode: String,
    val changeHouseComplexes: Int,
    val maleChangeHouses: Int,
    val femaleChangeHouses: Int,
    val unisexChangeHouses: Int,
    val workforceCapacity: Int,
    val complianceScore: Int,
    val statusLabel: String,
    val statusColor: Color
)

val MONITORED_MINING_SITES = listOf(
    MonitoredMiningSiteGis(
        id = "SITE_LETABA",
        name = "Letaba Crushers (Pty) Ltd",
        company = "Letaba Quarry Holdings",
        region = "Tzaneen, Limpopo",
        latitude = -23.8211,
        longitude = 30.1633,
        cadastralProperty = "Farm Tarentaalrant 24LT (Ptn 3 & 4)",
        miningRightCode = "LP30/5/1/2/2/14466MR",
        changeHouseComplexes = 4,
        maleChangeHouses = 2,
        femaleChangeHouses = 1,
        unisexChangeHouses = 1,
        workforceCapacity = 142,
        complianceScore = 94,
        statusLabel = "COMPLIANT",
        statusColor = ComplianceGreen
    ),
    MonitoredMiningSiteGis(
        id = "SITE_AFRIMAT",
        name = "Afrimat Kuipersbult Quarry",
        company = "Afrimat Aggregates (Pty) Ltd",
        region = "Lephalale / Ellisras, Limpopo",
        latitude = -23.7076,
        longitude = 27.5794,
        cadastralProperty = "Farm Kuipersbult 511LQ",
        miningRightCode = "LP30/5/1/2/2/181MR",
        changeHouseComplexes = 3,
        maleChangeHouses = 1,
        femaleChangeHouses = 1,
        unisexChangeHouses = 1,
        workforceCapacity = 88,
        complianceScore = 88,
        statusLabel = "AUDIT DUE",
        statusColor = ComplianceAmber
    ),
    MonitoredMiningSiteGis(
        id = "SITE_DWARSRIVIER",
        name = "Dwarsrivier Chrome Mine",
        company = "Dwarsrivier Operations (Assore)",
        region = "Steelpoort Valley, Sekhukhune",
        latitude = -24.9281,
        longitude = 30.1256,
        cadastralProperty = "Farm Dwarsrivier 372KT",
        miningRightCode = "LP30/5/1/2/2/179MR",
        changeHouseComplexes = 6,
        maleChangeHouses = 3,
        femaleChangeHouses = 2,
        unisexChangeHouses = 1,
        workforceCapacity = 450,
        complianceScore = 96,
        statusLabel = "COMPLIANT",
        statusColor = ComplianceGreen
    ),
    MonitoredMiningSiteGis(
        id = "SITE_GRASVALLY",
        name = "Grasvally Chromium Mine",
        company = "Grasvally Chrome (Pty) Ltd",
        region = "Mokopane, Waterberg District",
        latitude = -24.2389,
        longitude = 28.9812,
        cadastralProperty = "Farm Grasvally 293KR",
        miningRightCode = "LP30/5/1/2/2/215MR",
        changeHouseComplexes = 2,
        maleChangeHouses = 1,
        femaleChangeHouses = 1,
        unisexChangeHouses = 0,
        workforceCapacity = 65,
        complianceScore = 82,
        statusLabel = "IN PROGRESS",
        statusColor = WorkforceOrange
    )
)

/**
 * Builds an interactive HTML page hosting Leaflet.js and OpenStreetMap cadastral tiles
 * with custom pins for all monitored mining operations and change house complexes.
 */
fun buildLeafletGisHtml(initialSiteIndex: Int = 0): String {
    val sitesJson = StringBuilder("[")
    MONITORED_MINING_SITES.forEachIndexed { i, s ->
        sitesJson.append(
            """
            {
                "id": "${s.id}",
                "name": "${s.name}",
                "company": "${s.company}",
                "region": "${s.region}",
                "lat": ${s.latitude},
                "lng": ${s.longitude},
                "cadastral": "${s.cadastralProperty}",
                "code": "${s.miningRightCode}",
                "complexes": ${s.changeHouseComplexes},
                "male": ${s.maleChangeHouses},
                "female": ${s.femaleChangeHouses},
                "unisex": ${s.unisexChangeHouses},
                "capacity": ${s.workforceCapacity},
                "score": ${s.complianceScore},
                "status": "${s.statusLabel}"
            }
            """.trimIndent()
        )
        if (i < MONITORED_MINING_SITES.size - 1) sitesJson.append(",")
    }
    sitesJson.append("]")

    val defaultSite = MONITORED_MINING_SITES.getOrNull(initialSiteIndex) ?: MONITORED_MINING_SITES[0]

    return """
    <!DOCTYPE html>
    <html>
    <head>
        <meta charset="utf-8" />
        <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" />
        <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" />
        <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
        <style>
            html, body {
                height: 100%;
                margin: 0;
                padding: 0;
                background-color: #0B1120;
                overflow: hidden;
            }
            #map {
                width: 100%;
                height: 100%;
                background: #0B1120;
            }
            .leaflet-control-attribution {
                background: rgba(15, 23, 42, 0.6) !important;
                color: #64748B !important;
                font-size: 8px !important;
            }
            .leaflet-control-attribution a {
                color: #94A3B8 !important;
            }
            /* High-Visibility Pinned Location Marker */
            .pinned-marker-container {
                position: relative;
                display: flex;
                flex-direction: column;
                align-items: center;
                cursor: pointer;
            }
            .pin-pulse {
                position: absolute;
                bottom: 2px;
                left: 50%;
                transform: translateX(-50%);
                width: 18px;
                height: 9px;
                background: rgba(220, 38, 38, 0.7);
                border-radius: 50%;
                animation: pinPulse 2s infinite ease-out;
            }
            .pin-body {
                width: 34px;
                height: 34px;
                background: #DC2626;
                border: 2px solid #FFFFFF;
                border-radius: 50% 50% 50% 0;
                transform: rotate(-45deg);
                display: flex;
                align-items: center;
                justify-content: center;
                box-shadow: 0 4px 10px rgba(0, 0, 0, 0.5), 0 0 12px rgba(220, 38, 38, 0.85);
                transition: transform 0.25s ease, background 0.25s ease;
            }
            .pin-body.selected {
                background: #B91C1C;
                box-shadow: 0 6px 16px rgba(0, 0, 0, 0.7), 0 0 18px rgba(239, 68, 68, 1);
            }
            .pin-icon {
                transform: rotate(45deg);
                font-size: 15px;
                line-height: 1;
                color: #FFFFFF;
            }
            .changehouse-dot {
                width: 12px;
                height: 12px;
                border-radius: 50%;
                background: #0284C7;
                border: 2px solid #FFFFFF;
                box-shadow: 0 0 6px rgba(56, 189, 248, 0.9);
            }
            @keyframes pinPulse {
                0% { transform: translateX(-50%) scale(0.6); opacity: 1; }
                100% { transform: translateX(-50%) scale(2.5); opacity: 0; }
            }
        </style>
    </head>
    <body>
        <div id="map"></div>

        <script>
            var sites = $sitesJson;
            var initialLat = ${defaultSite.latitude};
            var initialLng = ${defaultSite.longitude};

            // Initialize OpenStreetMap Leaflet Map
            var map = L.map('map', {
                center: [initialLat, initialLng],
                zoom: 13,
                zoomControl: true
            });

            // Standard OSM Carto Tiles - completely unobstructed
            L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                maxZoom: 19,
                attribution: '&copy; OpenStreetMap'
            }).addTo(map);

            var markers = {};
            var activeSiteId = "${defaultSite.id}";

            sites.forEach(function(site, index) {
                var isSelected = (site.id === activeSiteId);
                var pinHtml = 
                    '<div class="pinned-marker-container" id="pin-wrap-' + site.id + '">' +
                        '<div class="pin-pulse"></div>' +
                        '<div class="pin-body ' + (isSelected ? 'selected' : '') + '">' +
                            '<span class="pin-icon">⛏</span>' +
                        '</div>' +
                    '</div>';

                var pinIcon = L.divIcon({
                    className: 'custom-pin-icon',
                    html: pinHtml,
                    iconSize: [34, 44],
                    iconAnchor: [17, 40]
                });

                var marker = L.marker([site.lat, site.lng], { icon: pinIcon }).addTo(map);
                markers[site.id] = marker;

                // Center on click and notify Android Compose without any blocking text popups
                marker.on('click', function() {
                    focusSite(site.id);
                    if (window.AndroidBridge && window.AndroidBridge.onPinClicked) {
                        window.AndroidBridge.onPinClicked(site.id);
                    }
                });

                // Cadastral Property Boundary (unobtrusive thin outline)
                var delta = 0.007;
                var cadastralCoords = [
                    [site.lat + delta, site.lng - delta],
                    [site.lat + delta * 1.1, site.lng + delta * 0.9],
                    [site.lat - delta * 0.9, site.lng + delta * 1.2],
                    [site.lat - delta * 1.1, site.lng - delta * 0.8]
                ];

                L.polygon(cadastralCoords, {
                    color: '#DC2626',
                    weight: 2,
                    fillColor: '#F97316',
                    fillOpacity: 0.08,
                    dashArray: '5, 5'
                }).addTo(map);

                // Change House Complex pins around the site
                for (var k = 0; k < site.complexes; k++) {
                    var offsetLat = (Math.sin(k * 1.6) * 0.0028);
                    var offsetLng = (Math.cos(k * 1.6) * 0.0028);
                    var chIcon = L.divIcon({
                        className: 'ch-dot-icon',
                        html: '<div class="changehouse-dot" title="Ablution Complex #' + (k+1) + '"></div>',
                        iconSize: [12, 12],
                        iconAnchor: [6, 6]
                    });
                    L.marker([site.lat + offsetLat, site.lng + offsetLng], { icon: chIcon }).addTo(map);
                }
            });

            function focusSite(siteId) {
                activeSiteId = siteId;
                var target = sites.find(function(s) { return s.id === siteId; });
                if (target && markers[siteId]) {
                    map.flyTo([target.lat, target.lng], 14, { duration: 0.9 });
                }
            }

            // Global JS function called from Kotlin Android
            window.focusSiteOnMap = function(siteId) {
                focusSite(siteId);
            };
        </script>
    </body>
    </html>
    """.trimIndent()
}

/**
 * Live Leaflet GIS Cadastral Mapping of Monitored Mining Operations and Change House Complexes
 * with OpenStreetMap, cadastral parcels, and site telemetry.
 */
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun DashboardGisMapComponent(
    modifier: Modifier = Modifier,
    onSelectSiteForInspection: (MonitoredMiningSiteGis) -> Unit = {}
) {
    var selectedSiteIndex by remember { mutableIntStateOf(0) }
    var webViewInstance by remember { mutableStateOf<WebView?>(null) }
    var isMapLoading by remember { mutableStateOf(true) }
    var isExpandedView by remember { mutableStateOf(false) }

    val currentSite = MONITORED_MINING_SITES[selectedSiteIndex]

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("dashboard_leaflet_gis_map_component"),
        colors = CardDefaults.cardColors(containerColor = WorkforceSurface),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, WorkforceCardBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header: Title & GIS Mapping Description
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(WorkforceRed.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Map,
                            contentDescription = null,
                            tint = WorkforceRed,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "GEOGRAPHICAL DISTRIBUTION OF ASSESSMENT SITES",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.6.sp,
                                color = WorkforceTextPrimary,
                                fontSize = 12.sp
                            )
                        )
                        Text(
                            text = "Live Leaflet GIS Cadastral Mapping • OpenStreetMap",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = WorkforceTextSecondary,
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = WorkforceOrange.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, WorkforceOrange.copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Layers,
                            contentDescription = null,
                            tint = WorkforceOrange,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "CADASTRAL",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = WorkforceOrange,
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Mine Property Selector Tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MONITORED_MINING_SITES.forEachIndexed { index, site ->
                    val isSelected = selectedSiteIndex == index
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) WorkforceRed else WorkforceSurfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) WorkforceRed else WorkforceCardBorder
                        ),
                        modifier = Modifier
                            .clickable {
                                selectedSiteIndex = index
                                webViewInstance?.evaluateJavascript("window.focusSiteOnMap('${site.id}');", null)
                            }
                            .testTag("mine_map_tab_${site.id}")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) Color.White else site.statusColor)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = site.name.substringBefore(" (").take(18),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else WorkforceTextPrimary,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Live Leaflet + OpenStreetMap Map Container (Completely Unobstructed)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (isExpandedView) 380.dp else 280.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, WorkforceCardBorder, RoundedCornerShape(12.dp))
                    .testTag("leaflet_osm_map_container")
            ) {
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { ctx ->
                        WebView(ctx).apply {
                            layoutParams = ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                            settings.apply {
                                javaScriptEnabled = true
                                domStorageEnabled = true
                                loadWithOverviewMode = true
                                useWideViewPort = true
                                builtInZoomControls = true
                                displayZoomControls = false
                                cacheMode = WebSettings.LOAD_DEFAULT
                            }
                            addJavascriptInterface(
                                object {
                                    @JavascriptInterface
                                    fun onPinClicked(siteId: String) {
                                        post {
                                            val idx = MONITORED_MINING_SITES.indexOfFirst { it.id == siteId }
                                            if (idx >= 0) {
                                                selectedSiteIndex = idx
                                            }
                                        }
                                    }
                                },
                                "AndroidBridge"
                            )
                            webViewClient = object : WebViewClient() {
                                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                                    isMapLoading = true
                                }
                                override fun onPageFinished(view: WebView?, url: String?) {
                                    isMapLoading = false
                                }
                                override fun onReceivedError(
                                    view: WebView?,
                                    request: WebResourceRequest?,
                                    error: WebResourceError?
                                ) {
                                    isMapLoading = false
                                }
                            }
                            loadDataWithBaseURL(
                                "https://unpkg.com",
                                buildLeafletGisHtml(selectedSiteIndex),
                                "text/html",
                                "UTF-8",
                                null
                            )
                            webViewInstance = this
                        }
                    },
                    update = { view ->
                        webViewInstance = view
                    }
                )

                // Map View Controls: Expand View & Center Selected Mine
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = WorkforceDarkBg.copy(alpha = 0.85f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, WorkforceCardBorder),
                        modifier = Modifier
                            .size(32.dp)
                            .clickable {
                                webViewInstance?.evaluateJavascript("window.focusSiteOnMap('${currentSite.id}');", null)
                            }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.MyLocation,
                                contentDescription = "Center Site",
                                tint = WorkforceOrange,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Surface(
                        shape = CircleShape,
                        color = WorkforceDarkBg.copy(alpha = 0.85f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, WorkforceCardBorder),
                        modifier = Modifier
                            .size(32.dp)
                            .clickable { isExpandedView = !isExpandedView }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Fullscreen,
                                contentDescription = "Expand Map",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Cadastral Telemetry & Monitored Complex Summary
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = WorkforceSurfaceVariant,
                border = androidx.compose.foundation.BorderStroke(1.dp, WorkforceCardBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("gis_selected_site_card")
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = currentSite.name,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = WorkforceTextPrimary
                                )
                            )
                            Text(
                                text = "${currentSite.cadastralProperty} • ${currentSite.miningRightCode}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = WorkforceOrange,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 11.sp
                                )
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = currentSite.statusColor.copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, currentSite.statusColor.copy(alpha = 0.5f))
                        ) {
                            Text(
                                text = "${currentSite.statusLabel} • ${currentSite.complianceScore}%",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = currentSite.statusColor,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Change House Breakdown Chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        GisInfoChip(
                            label = "Change Houses",
                            value = "${currentSite.changeHouseComplexes} Complexes",
                            icon = Icons.Default.Business,
                            modifier = Modifier.weight(1f)
                        )
                        GisInfoChip(
                            label = "Ablution Split",
                            value = "${currentSite.maleChangeHouses}M / ${currentSite.femaleChangeHouses}F / ${currentSite.unisexChangeHouses}U",
                            icon = Icons.Default.Wc,
                            modifier = Modifier.weight(1f)
                        )
                        GisInfoChip(
                            label = "Coordinates",
                            value = "${currentSite.latitude.toString().take(6)}, ${currentSite.longitude.toString().take(6)}",
                            icon = Icons.Default.LocationOn,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = { onSelectSiteForInspection(currentSite) },
                            colors = ButtonDefaults.buttonColors(containerColor = WorkforceRed),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("btn_inspect_site_${currentSite.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "AUDIT CHANGE HOUSE COMPLEX",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GisInfoChip(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = WorkforceDarkBg,
        shape = RoundedCornerShape(6.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, WorkforceCardBorder)
    ) {
        Column(modifier = Modifier.padding(6.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = WorkforceOrange,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = WorkforceTextMuted,
                        fontSize = 9.sp
                    ),
                    maxLines = 1
                )
            }
            Text(
                text = value,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = WorkforceTextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                ),
                maxLines = 1
            )
        }
    }
}
