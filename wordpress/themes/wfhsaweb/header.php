<?php
/**
 * Header template for WFHSA Web theme
 * URI: https://wfhsa.com/new/
 */
if (!defined('ABSPATH')) exit;
?><!DOCTYPE html>
<html <?php language_attributes(); ?>>
<head>
    <meta charset="<?php bloginfo('charset'); ?>">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=5.0">
    <meta name="theme-color" content="#0F1015">
    <?php wp_head(); ?>
</head>
<body <?php body_class(); ?>>
<?php wp_body_open(); ?>

<header class="wfhsa-header">
    <div class="wfhsa-branding">
        <a href="<?php echo esc_url(home_url('/')); ?>" style="display:flex; align-items:center; gap:12px; text-decoration:none;">
            <?php 
            $logo_url = get_template_directory_uri() . '/images/logo.png';
            ?>
            <img src="<?php echo esc_url($logo_url); ?>" alt="WFHSA Web - WorkForce Health SA" style="height: 44px; width: auto;" />
            <div>
                <div class="wfhsa-title">WFHSA WEB</div>
                <div class="wfhsa-subtitle">WorkForce Health SA &bull; Occupational Hygiene Hub</div>
            </div>
        </a>
    </div>

    <!-- Quick Navigation Links -->
    <nav class="wfhsa-nav-links" style="display: flex; gap: 8px;">
        <a href="<?php echo esc_url(home_url('/')); ?>" class="wfhsa-nav-item active">
            Dashboard
        </a>
        <a href="<?php echo esc_url(home_url('/?wfhsa_action=wizard')); ?>" class="wfhsa-nav-item">
            Location Selector
        </a>
        <a href="<?php echo esc_url(home_url('/?wfhsa_action=inspection')); ?>" class="wfhsa-nav-item">
            New Inspection
        </a>
    </nav>

    <!-- User Authentication & Role Badge -->
    <div style="display: flex; align-items: center; gap: 12px;">
        <div style="display: flex; align-items: center; gap: 6px; background: rgba(16, 185, 129, 0.1); border: 1px solid rgba(16, 185, 129, 0.3); padding: 4px 10px; border-radius: 999px;">
            <span style="display: inline-block; width: 8px; height: 8px; border-radius: 50%; background: #10B981;"></span>
            <span style="font-size: 0.75rem; color: #10B981; font-weight: 700;">LIVE SYNC</span>
        </div>

        <?php if (is_user_logged_in()): 
            $current_user = wp_get_current_user();
            $roles = (array) $current_user->roles;
            $raw_role = !empty($roles) ? $roles[0] : 'field_assessor';
            $role_name = ucwords(str_replace(array('wfhsaweb_', 'mobiworkforce_', 'wfhsa_', '_'), array('', '', '', ' '), $raw_role));
        ?>
            <span style="font-size: 0.85rem; color: var(--wfhsa-text); font-weight: 700;">
                <?php echo esc_html($current_user->display_name); ?>
            </span>
            <span class="badge" style="background:#202330; border:1px solid #2E3244; color:#FF6D00; font-size:11px; padding:3px 8px; border-radius:4px; font-weight:800;">
                <?php echo esc_html($role_name); ?>
            </span>
            <a href="<?php echo esc_url(wp_logout_url(home_url('/'))); ?>" class="wfhsa-btn wfhsa-btn-outline" style="font-size:0.75rem; padding:4px 8px;">
                Logout
            </a>
        <?php else: ?>
            <a href="<?php echo esc_url(wp_login_url(home_url('/'))); ?>" class="wfhsa-btn wfhsa-btn-primary" style="font-size:0.8rem; padding:6px 14px;">
                Assessor Sign In
            </a>
        <?php endif; ?>
    </div>
</header>
<main class="wfhsa-container">
