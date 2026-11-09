<?php
/**
 * Footer template for WFHSA Web theme
 * URI: https://wfhsa.com/new/
 */
if (!defined('ABSPATH')) exit;
?>

<footer class="wfhsa-footer">
    <div style="max-width: 1100px; margin: 0 auto; display: flex; flex-direction: column; gap: 10px;">
        <div style="display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 16px; border-bottom: 1px solid var(--wfhsa-border); padding-bottom: 16px;">
            <div style="text-align: left;">
                <div style="font-weight: 800; color: #FFFFFF; letter-spacing: 0.5px; font-size: 0.95rem;">
                    WFHSA WEB • OCCUPATIONAL HYGIENE DIGITAL PLATFORM
                </div>
                <div style="color: var(--wfhsa-orange); font-size: 0.8rem; font-weight: 700; margin-top: 2px;">
                    SAFER WORKPLACES &nbsp;|&nbsp; HEALTHIER PEOPLE &nbsp;|&nbsp; A STRONGER TOMORROW
                </div>
            </div>
            <div style="display: flex; gap: 10px;">
                <span class="wfhsa-badge badge-green">MHSA COMPLIANT</span>
                <span class="wfhsa-badge badge-orange">SAIOH CERTIFIED</span>
                <span class="wfhsa-badge badge-blue">OFFLINE-FIRST REST API</span>
            </div>
        </div>

        <div style="font-size: 0.78rem; color: var(--wfhsa-text-muted); line-height: 1.5;">
            Compliant with South African Mine Health and Safety Act (Act 29 of 1996), SAIOH Standards & POPIA.
            <br />
            Synchronized with Android Companion Inspection App via <code>https://wfhsa.com/new/wp-json/wfhsaweb/v1/sync</code>
        </div>

        <div style="font-size: 0.75rem; color: #64748B; margin-top: 4px;">
            &copy; <?php echo date('Y'); ?> WorkForce Health SA (Pty) Ltd. Package: WFHSA Web Suite v1.3.0. All rights reserved.
        </div>
    </div>
</footer>

<?php wp_footer(); ?>
</body>
</html>
