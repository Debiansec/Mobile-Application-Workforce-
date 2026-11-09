<?php
/**
 * Template Name: MobiWorkforce Dashboard Page
 * Description: Front-end management dashboard for MobiWorkforce & WorkForce Health SA
 */
get_header(); ?>

<main class="wfhsa-container">
    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; flex-wrap: wrap; gap: 14px;">
        <div>
            <div style="font-size: 0.8rem; font-weight: 800; color: var(--wfhsa-orange); letter-spacing: 1.5px;">
                MOBIWORKFORCE PLATFORM &bull; CONTROL HUB
            </div>
            <h1 style="font-size: 2rem; font-weight: 900; color: #FFFFFF; margin-top: 4px;">
                Inspection Hub & Field Mobile Sync
            </h1>
        </div>
        <div style="display: flex; gap: 10px; align-items: center;">
            <a href="<?php echo esc_url(home_url('/?mobi_action=inspection')); ?>" class="wfhsa-btn wfhsa-btn-primary">
                + Launch Inspection
            </a>
            <button onclick="window.print()" class="wfhsa-btn wfhsa-btn-secondary" style="font-size:0.85rem; padding: 10px 14px;">
                🖨️ Print Dashboard
            </button>
        </div>
    </div>

    <!-- 3-Step Location Wizard Component -->
    <div class="wfhsa-card" style="margin-bottom: 24px;">
        <?php 
        if (shortcode_exists('mobiworkforce_location_selector')) {
            echo do_shortcode('[mobiworkforce_location_selector]');
        } else {
            echo do_shortcode('[wfhsa_location_selector]');
        }
        ?>
    </div>

    <!-- Active Inspection Checklist Wizard -->
    <div class="wfhsa-card" style="margin-bottom: 24px;">
        <?php 
        if (shortcode_exists('mobiworkforce_new_inspection')) {
            echo do_shortcode('[mobiworkforce_new_inspection]');
        } else {
            echo do_shortcode('[wfhsa_new_inspection]');
        }
        ?>
    </div>

    <!-- Role Switcher & Security Verification -->
    <div class="wfhsa-card">
        <?php 
        if (shortcode_exists('mobiworkforce_role_switcher')) {
            echo do_shortcode('[mobiworkforce_role_switcher]');
        } else {
            echo do_shortcode('[wfhsa_role_switcher]');
        }
        ?>
    </div>
</main>

<?php get_footer(); ?>
