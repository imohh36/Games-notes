package com.spinel.gamenotes.ads

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.spinel.gamenotes.BuildConfig
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

/**
 * Standard AdMob Banner Composable.
 * Features:
 * - Clean containment compliant with Google Play Ad Policies (clear ad attribution).
 * - Automatic destroy/release on lifecycle cleanup.
 * - Safely omitted when BuildConfig.ENABLE_ADS is false.
 */
@Composable
fun BannerAdView(
    modifier: Modifier = Modifier,
    adSize: AdSize = AdSize.BANNER
) {
    if (!BuildConfig.ENABLE_ADS) return

    Surface(
        modifier = modifier
            .fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Subtle attribution badge compliant with Google Play Store policies
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(bottom = 2.dp)
            ) {
                Text(
                    text = "إعلان تجريبي • Ad",
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 9.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }

            AndroidView(
                modifier = Modifier.fillMaxWidth(),
                factory = { ctx ->
                    try {
                        AdView(ctx).apply {
                            setAdSize(adSize)
                            adUnitId = AdManager.TEST_BANNER_AD_ID
                            loadAd(AdRequest.Builder().build())
                        }
                    } catch (t: Throwable) {
                        android.util.Log.e("BannerAdView", "Failed to create AdView", t)
                        android.view.View(ctx)
                    }
                },
                update = { /* Keep existing ad */ },
                onRelease = { adView ->
                    try {
                        if (adView is AdView) {
                            adView.destroy()
                        }
                    } catch (t: Throwable) {
                        android.util.Log.e("BannerAdView", "Failed to destroy AdView", t)
                    }
                }
            )
        }
    }
}

/**
 * Inline Banner Ad displayed periodically between notes in the main list.
 * Styled as a card that harmonizes with NoteCard components.
 */
@Composable
fun InlineAdBanner(
    modifier: Modifier = Modifier
) {
    if (!BuildConfig.ENABLE_ADS) return

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)),
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "AD",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "إعلان مدمج بالقائمة",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                }

                Text(
                    text = "GameNotes",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    fontSize = 10.sp
                )
            }

            AndroidView(
                modifier = Modifier.fillMaxWidth(),
                factory = { ctx ->
                    try {
                        AdView(ctx).apply {
                            setAdSize(AdSize.LARGE_BANNER)
                            adUnitId = AdManager.TEST_BANNER_AD_ID
                            loadAd(AdRequest.Builder().build())
                        }
                    } catch (t: Throwable) {
                        android.util.Log.e("InlineAdBanner", "Failed to create AdView", t)
                        android.view.View(ctx)
                    }
                },
                update = { /* Keep existing ad */ },
                onRelease = { adView ->
                    try {
                        if (adView is AdView) {
                            adView.destroy()
                        }
                    } catch (t: Throwable) {
                        android.util.Log.e("InlineAdBanner", "Failed to destroy AdView", t)
                    }
                }
            )
        }
    }
}

/**
 * Sub-screen Banner Ad for secondary dialogs / screens (Trash, AI Chat History).
 */
@Composable
fun SubScreenBannerAd(
    modifier: Modifier = Modifier
) {
    if (!BuildConfig.ENABLE_ADS) return

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f)),
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "إعلان تجريبي • Ad",
                style = MaterialTheme.typography.labelSmall,
                fontSize = 9.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.padding(bottom = 2.dp)
            )

            AndroidView(
                modifier = Modifier.fillMaxWidth(),
                factory = { ctx ->
                    try {
                        AdView(ctx).apply {
                            setAdSize(AdSize.BANNER)
                            adUnitId = AdManager.TEST_BANNER_AD_ID
                            loadAd(AdRequest.Builder().build())
                        }
                    } catch (t: Throwable) {
                        android.util.Log.e("SubScreenBannerAd", "Failed to create AdView", t)
                        android.view.View(ctx)
                    }
                },
                update = { /* Keep existing ad */ },
                onRelease = { adView ->
                    try {
                        if (adView is AdView) {
                            adView.destroy()
                        }
                    } catch (t: Throwable) {
                        android.util.Log.e("SubScreenBannerAd", "Failed to destroy AdView", t)
                    }
                }
            )
        }
    }
}
