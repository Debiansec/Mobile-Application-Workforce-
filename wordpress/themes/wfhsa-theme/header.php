<?php
/**
 * Header template for WorkForce Health SA theme
 */
if (!defined('ABSPATH')) exit;
?><!DOCTYPE html>
<html <?php language_attributes(); ?>>
<head>
    <meta charset="<?php bloginfo('charset'); ?>">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <?php wp_head(); ?>
</head>
<body <?php body_class(); ?>>
<?php wp_body_open(); ?>

<header class="wfhsa-header">
    <div class="wfhsa-branding">
        <a href="<?php echo esc_url(home_url('/')); ?>">
            <img src="<?php echo esc_url(get_template_directory_uri() . '/images/logo.png'); ?>" alt="WorkForce Health SA Logo" />
        </a>
        <div>
            <div class="wfhsa-title">WORKFORCE HEALTH SA</div>
            <div class="wfhsa-subtitle">Occupational Hygiene Digital Platform</div>
        </div>
    </div>

    <div style="display: flex; align-items: center; gap: 14px;">
        <?php if (is_user_logged_in()): 
            $current_user = wp_get_current_user();
            $roles = (array) $current_user->roles;
            $role_name = !empty($roles) ? ucwords(str_replace(array('wfhsa_', '_'), array('', ' '), $roles[0])) : 'User';
        ?>
            <span style="font-size: 0.85rem; color: var(--wfhsa-text); font-weight: 600;">
                <?php echo esc_html($current_user->display_name); ?>
            </span>
            <span class="wfhsa-badge badge-orange"><?php echo esc_html($role_name); ?></span>
            <a href="<?php echo esc_url(wp_logout_url(home_url())); ?>" class="wfhsa-btn wfhsa-btn-secondary" style="min-height: 36px; padding: 6px 12px; font-size: 0.8rem;">
                Logout
            </a>
        <?php else: ?>
            <a href="<?php echo esc_url(wp_login_url()); ?>" class="wfhsa-btn wfhsa-btn-primary" style="min-height: 38px; padding: 8px 16px;">
                Assessor Sign-In
            </a>
        <?php endif; ?>
    </div>
</header>
