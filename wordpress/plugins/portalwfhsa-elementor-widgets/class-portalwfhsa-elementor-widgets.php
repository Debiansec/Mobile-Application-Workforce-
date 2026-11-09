<?php
/**
 * Elementor Widget Implementations
 */

if (!defined('ABSPATH')) {
    exit;
}

class Portalwfhsa_Dashboard_Widget extends \Elementor\Widget_Base {
    public function get_name() { return 'portalwfhsa_dashboard_widget'; }
    public function get_title() { return __('WFHSA Inspection Analytics', 'portalwfhsa'); }
    public function get_icon() { return 'eicon-dashboard'; }
    public function get_categories() { return array('portalwfhsa-category'); }

    protected function render() {
        echo '<div class="portalwfhsa-dashboard-elementor" style="background:#171922; border:1px solid #2E3244; border-radius:10px; padding:20px;">';
        echo '<div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:16px;">';
        echo '<h3 style="color:#FFF; margin:0; font-size:1.2rem; font-weight:800;">Mining Ablution Compliance Metrics</h3>';
        echo '<span style="background:rgba(16,185,129,0.2); color:#10B981; padding:4px 10px; border-radius:20px; font-weight:700; font-size:0.75rem;">LIVE TELEMETRY</span>';
        echo '</div>';
        echo '<div style="display:grid; grid-template-columns:repeat(auto-fit, minmax(200px, 1fr)); gap:12px;">';
        echo '<div style="background:#202330; padding:14px; border-radius:8px;"><span style="color:#94A3B8; font-size:0.75rem; font-weight:700;">TOTAL INSPECTIONS</span><div style="color:#FFF; font-size:1.6rem; font-weight:900;">51</div></div>';
        echo '<div style="background:#202330; padding:14px; border-radius:8px;"><span style="color:#94A3B8; font-size:0.75rem; font-weight:700;">CONFORMANCE SCORE</span><div style="color:#10B981; font-size:1.6rem; font-weight:900;">87.5%</div></div>';
        echo '<div style="background:#202330; padding:14px; border-radius:8px;"><span style="color:#94A3B8; font-size:0.75rem; font-weight:700;">REMEDIAL ACTIONS</span><div style="color:#FF6D00; font-size:1.6rem; font-weight:900;">3 Open</div></div>';
        echo '<div style="background:#202330; padding:14px; border-radius:8px;"><span style="color:#94A3B8; font-size:0.75rem; font-weight:700;">COMPULSORY REMARKS</span><div style="color:#10B981; font-size:1.6rem; font-weight:900;">100% OK</div></div>';
        echo '</div>';
        echo '</div>';
    }
}

class Portalwfhsa_Checklist_Widget extends \Elementor\Widget_Base {
    public function get_name() { return 'portalwfhsa_checklist_widget'; }
    public function get_title() { return __('WFHSA Ablution Checklist Matrix', 'portalwfhsa'); }
    public function get_icon() { return 'eicon-table'; }
    public function get_categories() { return array('portalwfhsa-category'); }

    protected function render() {
        echo '<div style="background:#171922; border:1px solid #2E3244; border-radius:10px; padding:16px;">';
        echo '<h4 style="color:#FFF; margin-bottom:12px;">Ablution Checklist & Compulsory Remarks Matrix (28-Page Master Template)</h4>';
        echo '<p style="color:#94A3B8; font-size:0.85rem;">Every question requires a mandatory typed remark or voice dictation with automatic GPS coordinate stamping.</p>';
        echo '</div>';
    }
}

class Portalwfhsa_Location_Selector_Widget extends \Elementor\Widget_Base {
    public function get_name() { return 'portalwfhsa_location_widget'; }
    public function get_title() { return __('WFHSA Mine Location Selector', 'portalwfhsa'); }
    public function get_icon() { return 'eicon-map-pin'; }
    public function get_categories() { return array('portalwfhsa-category'); }

    protected function render() {
        echo '<div style="background:#171922; border:1px solid #2E3244; border-radius:10px; padding:16px;">';
        echo '<h4 style="color:#FF6D00; margin-bottom:8px;">Customer, Site & Ablution Facility Selector</h4>';
        echo '<p style="color:#FFF; font-size:0.85rem;">Letaba Crushers (Pty) Ltd • Afrimat CM North (Kuipersbult)</p>';
        echo '</div>';
    }
}

class Portalwfhsa_Remedial_Actions_Widget extends \Elementor\Widget_Base {
    public function get_name() { return 'portalwfhsa_remedials_widget'; }
    public function get_title() { return __('WFHSA Remedial Actions Table', 'portalwfhsa'); }
    public function get_icon() { return 'eicon-alert'; }
    public function get_categories() { return array('portalwfhsa-category'); }

    protected function render() {
        echo '<div style="background:#171922; border:1px solid #2E3244; border-radius:10px; padding:16px;">';
        echo '<h4 style="color:#D32F2F; margin-bottom:8px;">Section 10 Remedial Close-Out Register</h4>';
        echo '<p style="color:#94A3B8; font-size:0.85rem;">Responsible persons, corrective actions, and target completion dates.</p>';
        echo '</div>';
    }
}
