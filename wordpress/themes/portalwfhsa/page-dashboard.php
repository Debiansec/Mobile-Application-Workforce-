<?php
/**
 * Template Name: Portal WFHSA Dashboard
 */
get_header(); ?>

<main class="portalwfhsa-container">
    <div style="margin-bottom: 24px;">
        <h1 style="font-size: 1.8rem; font-weight: 900; color: #FFFFFF;">Ablution Facilities Management Dashboard</h1>
        <p style="color: var(--wfhsa-text-muted); font-size: 0.9rem;">
            Real-time compliance monitoring, 28-page master inspection records, and Android field telemetry.
        </p>
    </div>

    <?php if (shortcode_exists('portalwfhsa_dashboard')) {
        echo do_shortcode('[portalwfhsa_dashboard]');
    } else { ?>
        <div class="portalwfhsa-grid">
            <div class="portalwfhsa-stat-card">
                <span class="portalwfhsa-stat-label">Total Facilities Audited</span>
                <span class="portalwfhsa-stat-value">18</span>
                <span class="portalwfhsa-stat-desc">Male, Female, Unisex & Change Houses</span>
            </div>
            <div class="portalwfhsa-stat-card">
                <span class="portalwfhsa-stat-label">Compliance Trend (30 Days)</span>
                <span class="portalwfhsa-stat-value" style="color: var(--wfhsa-green);">+6.2%</span>
                <span class="portalwfhsa-stat-desc">Steady improvement across mine sites</span>
            </div>
            <div class="portalwfhsa-stat-card">
                <span class="portalwfhsa-stat-label">Outstanding Remedial Actions</span>
                <span class="portalwfhsa-stat-value" style="color: var(--wfhsa-orange);">4</span>
                <span class="portalwfhsa-stat-desc">Assigned to engineering maintenance</span>
            </div>
            <div class="portalwfhsa-stat-card">
                <span class="portalwfhsa-stat-label">Live Sync Server</span>
                <span class="portalwfhsa-stat-value" style="font-size: 1.1rem; color: var(--wfhsa-blue);">wfhsa.com/new</span>
                <span class="portalwfhsa-stat-desc">Android app buffer active</span>
            </div>
        </div>
    <?php } ?>
</main>

<?php get_footer(); ?>
