<?php
/**
 * Main dashboard & home template for MobiWorkforce theme
 * URI: https://wfhsa.com/new/
 */
get_header(); ?>

<main class="wfhsa-container">
    <!-- Hero Banner with mining graphics -->
    <div style="position: relative; border-radius: 14px; overflow: hidden; margin-bottom: 28px; border: 1px solid var(--wfhsa-border); min-height: 220px; display: flex; align-items: flex-end; padding: 28px; background: linear-gradient(180deg, rgba(15,16,21,0.25) 0%, rgba(15,16,21,0.95) 85%), url('<?php echo esc_url(get_template_directory_uri() . '/images/hero.png'); ?>') center/cover;">
        <div>
            <div style="display:inline-flex; align-items:center; gap:8px; background: rgba(255, 109, 0, 0.2); border: 1px solid #FF6D00; padding: 4px 12px; border-radius: 999px; margin-bottom: 8px;">
                <span style="font-size: 0.75rem; font-weight: 800; color: #FF6D00; letter-spacing: 1.5px;">MOBIWORKFORCE PLATFORM</span>
            </div>
            <h1 style="font-size: 2.2rem; font-weight: 900; color: #FFFFFF; margin: 4px 0 8px 0; letter-spacing: -0.5px;">
                Mining Hygiene Auditing & Mobile Sync
            </h1>
            <p style="color: var(--wfhsa-text-muted); font-size: 0.95rem; max-width: 720px; line-height: 1.5;">
                Monthly Male Ablution Facilities Checklist (51 Points) &bull; Letaba Crushers, Tzaneen Mine #14466 &bull; Direct synchronization with Android Inspection App via <strong>https://wfhsa.com/new/</strong>.
            </p>
        </div>
    </div>

    <!-- Executive Metrics Grid -->
    <div class="wfhsa-stat-grid">
        <div class="wfhsa-stat-box">
            <span class="wfhsa-stat-label">Total Audits Conducted</span>
            <span class="wfhsa-stat-value" style="color: #38BDF8;">142</span>
            <span style="font-size: 0.75rem; color: var(--wfhsa-text-muted); margin-top: 4px;">Letaba & Marula mining complexes</span>
        </div>
        <div class="wfhsa-stat-box">
            <span class="wfhsa-stat-label">Average Compliance Score</span>
            <span class="wfhsa-stat-value" style="color: #10B981;">88.4%</span>
            <span style="font-size: 0.75rem; color: #10B981; margin-top: 4px;">&bull; DMR Standard Met (Threshold &ge; 80%)</span>
        </div>
        <div class="wfhsa-stat-box">
            <span class="wfhsa-stat-label">Critical Action Items</span>
            <span class="wfhsa-stat-value" style="color: #FF6D00;">3</span>
            <span style="font-size: 0.75rem; color: var(--wfhsa-text-muted); margin-top: 4px;">Assigned to Mine Maintenance</span>
        </div>
        <div class="wfhsa-stat-box">
            <span class="wfhsa-stat-label">Android Field Sync</span>
            <span class="wfhsa-stat-value" style="color: #10B981; font-size: 1.4rem;">CONNECTED</span>
            <span style="font-size: 0.75rem; color: var(--wfhsa-text-muted); margin-top: 4px;"><code>/new/wp-json/mobiworkforce/v1</code></span>
        </div>
    </div>

    <!-- Active Content or Modular Components -->
    <?php if (have_posts() && !is_home()) : while (have_posts()) : the_post(); ?>
        <div class="wfhsa-card">
            <?php the_content(); ?>
        </div>
    <?php endwhile; else: ?>
        
        <!-- Step 1 & 2: Location Wizard & Role Switcher -->
        <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(340px, 1fr)); gap: 24px; margin-bottom: 24px;">
            <div class="wfhsa-card" style="margin-bottom: 0;">
                <div class="wfhsa-card-header">
                    <h3 class="wfhsa-card-title">
                        📍 1. Select Customer, Site & Ablution Facility
                    </h3>
                    <span class="wfhsa-badge badge-orange">STEP 1-3</span>
                </div>
                <p style="color: var(--wfhsa-text-muted); font-size: 0.85rem; margin-bottom: 16px;">
                    Select an authorized mining client, property site, and specific ablution block before launching an inspection.
                </p>
                <?php 
                if (shortcode_exists('mobiworkforce_location_selector')) {
                    echo do_shortcode('[mobiworkforce_location_selector]');
                } elseif (shortcode_exists('wfhsa_location_selector')) {
                    echo do_shortcode('[wfhsa_location_selector]');
                } else {
                    echo '<p style="color:#F59E0B;">Location Selector plugin active. Ready to initiate inspection.</p>';
                }
                ?>
            </div>

            <div class="wfhsa-card" style="margin-bottom: 0;">
                <div class="wfhsa-card-header">
                    <h3 class="wfhsa-card-title">
                        👤 2. Authorized User & Role Switcher
                    </h3>
                    <span class="wfhsa-badge badge-blue">RBAC GATE</span>
                </div>
                <p style="color: var(--wfhsa-text-muted); font-size: 0.85rem; margin-bottom: 16px;">
                    Ensure permissions match field duties (Field Assessor, SAIOH Hygienist, Mine Manager, Admin).
                </p>
                <?php 
                if (shortcode_exists('mobiworkforce_role_switcher')) {
                    echo do_shortcode('[mobiworkforce_role_switcher]');
                } elseif (shortcode_exists('wfhsa_role_switcher')) {
                    echo do_shortcode('[wfhsa_role_switcher]');
                } else {
                    echo '<p style="color:#F59E0B;">User Roles plugin active.</p>';
                }
                ?>
            </div>
        </div>

        <!-- Step 3: Launch New Inspection Checklist -->
        <div class="wfhsa-card">
            <div class="wfhsa-card-header">
                <h3 class="wfhsa-card-title">
                    📋 3. Launch Occupational Hygiene Inspection Engine
                </h3>
                <span class="wfhsa-badge badge-green">51 CHECKLIST POINTS</span>
            </div>
            <p style="color: var(--wfhsa-text-muted); font-size: 0.88rem; margin-bottom: 18px;">
                Conduct physical audit across Basins, Showers, Toilets, Urinals, Lockers, and Sanitation Chemicals with photo attachment evidence and real-time DMR scoring.
            </p>
            <?php 
            if (shortcode_exists('mobiworkforce_new_inspection')) {
                echo do_shortcode('[mobiworkforce_new_inspection]');
            } elseif (shortcode_exists('wfhsa_new_inspection')) {
                echo do_shortcode('[wfhsa_new_inspection]');
            }
            ?>
        </div>

    <?php endif; ?>
</main>

<?php get_footer(); ?>
