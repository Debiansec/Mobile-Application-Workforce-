import React, { useState } from 'react';
import Head from 'next/head';

interface AssessmentRecord {
  id: string;
  referenceNumber: string;
  customerName: string;
  siteName: string;
  facilityName: string;
  assessorName: string;
  date: string;
  score: number;
  status: 'DRAFT' | 'IN_PROGRESS' | 'SUBMITTED_FOR_REVIEW' | 'APPROVED' | 'RETURNED_FOR_CORRECTION';
  nonCompliances: number;
}

const mockAssessments: AssessmentRecord[] = [
  {
    id: 'asm_letaba_01',
    referenceNumber: 'WFH-OH-2026-0042',
    customerName: 'Letaba Crushers (Pty) Ltd',
    siteName: 'Main Quarry & Plant (Mine #14466)',
    facilityName: 'Main Change House — Male',
    assessorName: 'Nomvula Khumalo',
    date: '2026-10-29',
    score: 85,
    status: 'SUBMITTED_FOR_REVIEW',
    nonCompliances: 2,
  },
  {
    id: 'asm_marula_01',
    referenceNumber: 'WFH-OH-2026-0039',
    customerName: 'Marula Platinum Mine (Pty) Ltd',
    siteName: 'Driekop Shaft & Concentrator',
    facilityName: 'Administration Complex Change House',
    assessorName: 'Sipho Sithole',
    date: '2026-10-24',
    score: 96,
    status: 'APPROVED',
    nonCompliances: 1,
  },
];

export default function WebManagementPortal() {
  const [selectedAssessment, setSelectedAssessment] = useState<AssessmentRecord | null>(mockAssessments[0]);
  const [activeTab, setActiveTab] = useState<'DASHBOARD' | 'ASSESSMENTS' | 'CUSTOMERS' | 'TEMPLATES' | 'EQUIPMENT'>('DASHBOARD');
  const [reviewNotes, setReviewNotes] = useState('Reviewed by Occupational Hygienist. All Section F chemical registers and SDS files conform to MHSA standards.');
  const [statusMessage, setStatusMessage] = useState('');

  const handleApprove = () => {
    if (selectedAssessment) {
      selectedAssessment.status = 'APPROVED';
      setStatusMessage(`Assessment ${selectedAssessment.referenceNumber} has been authorized and approved.`);
    }
  };

  const handleReturn = () => {
    if (selectedAssessment) {
      selectedAssessment.status = 'RETURNED_FOR_CORRECTION';
      setStatusMessage(`Assessment ${selectedAssessment.referenceNumber} returned to assessor for correction.`);
    }
  };

  return (
    <div style={{ minHeight: '100vh', backgroundColor: '#0B0D13', color: '#F8FAFC', fontFamily: 'system-ui, sans-serif' }}>
      <Head>
        <title>WORKFORCE HEALTH SA — Management Portal</title>
      </Head>

      {/* Top Navbar */}
      <header style={{ backgroundColor: '#171922', borderBottom: '1px solid #2E3244', padding: '16px 24px', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '16px' }}>
          <div style={{ backgroundColor: '#D32F2F', color: '#FFFFFF', padding: '6px 14px', borderRadius: '8px', fontWeight: 'bold', fontSize: '14px', letterSpacing: '1px' }}>
            WORKFORCE HEALTH SA
          </div>
          <span style={{ color: '#94A3B8', fontSize: '14px' }}>Occupational Hygiene Cloud Management Portal</span>
        </div>
        <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
          <span style={{ fontSize: '13px', color: '#F8FAFC' }}>Riaan Steyn (ROH 0482)</span>
          <span style={{ backgroundColor: 'rgba(16, 185, 129, 0.2)', color: '#10B981', border: '1px solid #10B981', padding: '2px 8px', borderRadius: '4px', fontSize: '11px', fontWeight: 'bold' }}>
            HYGIENIST
          </span>
        </div>
      </header>

      {/* Main Content Layout */}
      <div style={{ display: 'flex', minHeight: 'calc(100vh - 65px)' }}>
        {/* Sidebar Nav */}
        <aside style={{ width: '240px', backgroundColor: '#12141C', borderRight: '1px solid #2E3244', padding: '20px 12px' }}>
          <nav style={{ display: 'flex', flexDirection: 'column', gap: '6px' }}>
            <button
              onClick={() => setActiveTab('DASHBOARD')}
              style={{ textAlign: 'left', padding: '10px 14px', borderRadius: '6px', border: 'none', backgroundColor: activeTab === 'DASHBOARD' ? '#D32F2F' : 'transparent', color: '#FFFFFF', cursor: 'pointer', fontWeight: '600' }}
            >
              Dashboard Overview
            </button>
            <button
              onClick={() => setActiveTab('ASSESSMENTS')}
              style={{ textAlign: 'left', padding: '10px 14px', borderRadius: '6px', border: 'none', backgroundColor: activeTab === 'ASSESSMENTS' ? '#D32F2F' : 'transparent', color: '#FFFFFF', cursor: 'pointer', fontWeight: '600' }}
            >
              Assessments & Reviews
            </button>
            <button
              onClick={() => setActiveTab('CUSTOMERS')}
              style={{ textAlign: 'left', padding: '10px 14px', borderRadius: '6px', border: 'none', backgroundColor: activeTab === 'CUSTOMERS' ? '#D32F2F' : 'transparent', color: '#FFFFFF', cursor: 'pointer', fontWeight: '600' }}
            >
              Customers & Mines
            </button>
            <button
              onClick={() => setActiveTab('TEMPLATES')}
              style={{ textAlign: 'left', padding: '10px 14px', borderRadius: '6px', border: 'none', backgroundColor: activeTab === 'TEMPLATES' ? '#D32F2F' : 'transparent', color: '#FFFFFF', cursor: 'pointer', fontWeight: '600' }}
            >
              Checklist Templates
            </button>
            <button
              onClick={() => setActiveTab('EQUIPMENT')}
              style={{ textAlign: 'left', padding: '10px 14px', borderRadius: '6px', border: 'none', backgroundColor: activeTab === 'EQUIPMENT' ? '#D32F2F' : 'transparent', color: '#FFFFFF', cursor: 'pointer', fontWeight: '600' }}
            >
              Equipment Register
            </button>
          </nav>
        </aside>

        {/* Workspace */}
        <main style={{ flex: 1, padding: '28px', overflowY: 'auto' }}>
          {statusMessage && (
            <div style={{ backgroundColor: 'rgba(16, 185, 129, 0.15)', border: '1px solid #10B981', color: '#10B981', padding: '12px 18px', borderRadius: '8px', marginBottom: '20px' }}>
              {statusMessage}
            </div>
          )}

          {/* Metric Cards Header */}
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(4, 1fr)', gap: '16px', marginBottom: '28px' }}>
            <div style={{ backgroundColor: '#171922', border: '1px solid #2E3244', borderRadius: '10px', padding: '18px' }}>
              <div style={{ fontSize: '13px', color: '#94A3B8' }}>Total Inspections</div>
              <div style={{ fontSize: '28px', fontWeight: 'bold', marginTop: '4px' }}>42</div>
            </div>
            <div style={{ backgroundColor: '#171922', border: '1px solid #2E3244', borderRadius: '10px', padding: '18px' }}>
              <div style={{ fontSize: '13px', color: '#94A3B8' }}>Awaiting Review</div>
              <div style={{ fontSize: '28px', fontWeight: 'bold', marginTop: '4px', color: '#38BDF8' }}>1</div>
            </div>
            <div style={{ backgroundColor: '#171922', border: '1px solid #2E3244', borderRadius: '10px', padding: '18px' }}>
              <div style={{ fontSize: '13px', color: '#94A3B8' }}>Approved Reports</div>
              <div style={{ fontSize: '28px', fontWeight: 'bold', marginTop: '4px', color: '#10B981' }}>38</div>
            </div>
            <div style={{ backgroundColor: '#171922', border: '1px solid #2E3244', borderRadius: '10px', padding: '18px' }}>
              <div style={{ fontSize: '13px', color: '#94A3B8' }}>Open Remedial Actions</div>
              <div style={{ fontSize: '28px', fontWeight: 'bold', marginTop: '4px', color: '#EF4444' }}>3</div>
            </div>
          {/* Visual Progress & Trend Charts */}
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 2fr', gap: '20px', marginBottom: '28px' }}>
            {/* Chart 1: Completed vs Pending Inspections */}
            <div style={{ backgroundColor: '#171922', border: '1px solid #2E3244', borderRadius: '12px', padding: '20px' }}>
              <div style={{ fontSize: '11px', color: '#FF6D00', fontWeight: 'bold', letterSpacing: '0.5px' }}>STATUS PROGRESSION</div>
              <div style={{ fontSize: '16px', fontWeight: 'bold', marginBottom: '16px', color: '#F8FAFC' }}>Completed vs Pending Inspections</div>
              
              <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-around' }}>
                <svg width="150" height="150" viewBox="0 0 100 100">
                  <circle cx="50" cy="50" r="38" fill="transparent" stroke="#262C3D" strokeWidth="12" />
                  {/* Approved 38/42 = 90.5% -> strokeDasharray */}
                  <circle
                    cx="50"
                    cy="50"
                    r="38"
                    fill="transparent"
                    stroke="#10B981"
                    strokeWidth="12"
                    strokeDasharray="216 238"
                    strokeDashoffset="0"
                    transform="rotate(-90 50 50)"
                  />
                  {/* Awaiting Review 1/42 = 2.4% */}
                  <circle
                    cx="50"
                    cy="50"
                    r="38"
                    fill="transparent"
                    stroke="#38BDF8"
                    strokeWidth="12"
                    strokeDasharray="8 238"
                    strokeDashoffset="-216"
                    transform="rotate(-90 50 50)"
                  />
                  {/* In Progress 3/42 = 7.1% */}
                  <circle
                    cx="50"
                    cy="50"
                    r="38"
                    fill="transparent"
                    stroke="#FF6D00"
                    strokeWidth="12"
                    strokeDasharray="14 238"
                    strokeDashoffset="-224"
                    transform="rotate(-90 50 50)"
                  />
                  <text x="50" y="47" textAnchor="middle" fill="#FFFFFF" fontSize="14" fontWeight="bold">42</text>
                  <text x="50" y="59" textAnchor="middle" fill="#94A3B8" fontSize="7">AUDITS</text>
                </svg>

                <div style={{ display: 'flex', flexDirection: 'column', gap: '8px', fontSize: '12px' }}>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                    <span style={{ width: '10px', height: '10px', borderRadius: '50%', backgroundColor: '#10B981' }}></span>
                    <span style={{ color: '#94A3B8' }}>Approved (Final):</span>
                    <strong style={{ color: '#FFFFFF' }}>38 (90%)</strong>
                  </div>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                    <span style={{ width: '10px', height: '10px', borderRadius: '50%', backgroundColor: '#38BDF8' }}></span>
                    <span style={{ color: '#94A3B8' }}>Awaiting Review:</span>
                    <strong style={{ color: '#FFFFFF' }}>1 (2%)</strong>
                  </div>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                    <span style={{ width: '10px', height: '10px', borderRadius: '50%', backgroundColor: '#FF6D00' }}></span>
                    <span style={{ color: '#94A3B8' }}>In Progress:</span>
                    <strong style={{ color: '#FFFFFF' }}>3 (8%)</strong>
                  </div>
                </div>
              </div>
            </div>

            {/* Chart 2: Compliance Trends over Last 30 Days */}
            <div style={{ backgroundColor: '#171922', border: '1px solid #2E3244', borderRadius: '12px', padding: '20px' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '8px' }}>
                <div>
                  <div style={{ fontSize: '11px', color: '#FF6D00', fontWeight: 'bold', letterSpacing: '0.5px' }}>STATUTORY AUDIT PROGRESSION</div>
                  <div style={{ fontSize: '16px', fontWeight: 'bold', color: '#F8FAFC' }}>Compliance Trends (Last 30 Days)</div>
                </div>
                <div style={{ backgroundColor: 'rgba(239, 68, 68, 0.15)', border: '1px solid rgba(239, 68, 68, 0.4)', borderRadius: '6px', padding: '4px 8px', fontSize: '11px', color: '#EF4444', fontWeight: 'bold' }}>
                  80% MHSA Pass Mark
                </div>
              </div>

              {/* Area & Trend Line SVG */}
              <div style={{ height: '140px', width: '100%' }}>
                <svg width="100%" height="100%" viewBox="0 0 500 120" preserveAspectRatio="none">
                  <defs>
                    <linearGradient id="areaGradient" x1="0" y1="0" x2="0" y2="1">
                      <stop offset="0%" stopColor="#10B981" stopOpacity="0.3" />
                      <stop offset="100%" stopColor="#10B981" stopOpacity="0.0" />
                    </linearGradient>
                  </defs>
                  
                  {/* Grid lines */}
                  <line x1="0" y1="20" x2="500" y2="20" stroke="rgba(255,255,255,0.06)" strokeWidth="1" />
                  <line x1="0" y1="60" x2="500" y2="60" stroke="rgba(255,255,255,0.06)" strokeWidth="1" />
                  <line x1="0" y1="100" x2="500" y2="100" stroke="rgba(255,255,255,0.06)" strokeWidth="1" />

                  {/* 80% Threshold Dashed Line (Score 80 is at Y=48) */}
                  <line x1="0" y1="48" x2="500" y2="48" stroke="#EF4444" strokeWidth="1.5" strokeDasharray="6,4" />

                  {/* Area fill */}
                  <polygon
                    points="0,120 0,44 45,40 90,52 135,36 180,30 225,38 270,32 315,26 360,50 405,34 450,22 500,28 500,120"
                    fill="url(#areaGradient)"
                  />

                  {/* Trend line path */}
                  <polyline
                    fill="none"
                    stroke="#10B981"
                    strokeWidth="3"
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    points="0,44 45,40 90,52 135,36 180,30 225,38 270,32 315,26 360,50 405,34 450,22 500,28"
                  />

                  {/* Data Points */}
                  {[[0,44],[45,40],[90,52],[135,36],[180,30],[225,38],[270,32],[315,26],[360,50],[405,34],[450,22],[500,28]].map(([x,y], i) => (
                    <circle key={i} cx={x} cy={y} r="3.5" fill="#10B981" stroke="#FFFFFF" strokeWidth="1.5" />
                  ))}
                </svg>
              </div>

              <div style={{ display: 'flex', justifyContent: 'space-between', color: '#94A3B8', fontSize: '11px', marginTop: '6px' }}>
                <span>Oct 01 (82%)</span>
                <span>Oct 10 (88%)</span>
                <span>Oct 20 (92%)</span>
                <span style={{ color: '#10B981', fontWeight: 'bold' }}>Oct 31 (95% Compliant)</span>
              </div>
            </div>
          </div>

          {/* Detailed Review Table */}
          <div style={{ backgroundColor: '#171922', border: '1px solid #2E3244', borderRadius: '12px', padding: '24px' }}>
            <h2 style={{ fontSize: '18px', fontWeight: 'bold', marginBottom: '16px' }}>Inspection Review Queue</h2>

            <table style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'left', fontSize: '14px' }}>
              <thead>
                <tr style={{ borderBottom: '1px solid #2E3244', color: '#94A3B8' }}>
                  <th style={{ padding: '12px 8px' }}>Reference</th>
                  <th style={{ padding: '12px 8px' }}>Customer & Site</th>
                  <th style={{ padding: '12px 8px' }}>Facility</th>
                  <th style={{ padding: '12px 8px' }}>Assessor</th>
                  <th style={{ padding: '12px 8px' }}>Score</th>
                  <th style={{ padding: '12px 8px' }}>Status</th>
                  <th style={{ padding: '12px 8px' }}>Action</th>
                </tr>
              </thead>
              <tbody>
                {mockAssessments.map((a) => (
                  <tr key={a.id} style={{ borderBottom: '1px solid rgba(46, 50, 68, 0.4)' }}>
                    <td style={{ padding: '12px 8px', fontWeight: 'bold', color: '#FF6D00' }}>{a.referenceNumber}</td>
                    <td style={{ padding: '12px 8px' }}>{a.customerName}<br /><span style={{ fontSize: '12px', color: '#94A3B8' }}>{a.siteName}</span></td>
                    <td style={{ padding: '12px 8px' }}>{a.facilityName}</td>
                    <td style={{ padding: '12px 8px' }}>{a.assessorName}</td>
                    <td style={{ padding: '12px 8px', fontWeight: 'bold', color: a.score >= 80 ? '#10B981' : '#F59E0B' }}>{a.score}%</td>
                    <td style={{ padding: '12px 8px' }}>
                      <span style={{ padding: '4px 8px', borderRadius: '4px', fontSize: '11px', fontWeight: 'bold', backgroundColor: a.status === 'APPROVED' ? 'rgba(16, 185, 129, 0.2)' : 'rgba(56, 189, 248, 0.2)', color: a.status === 'APPROVED' ? '#10B981' : '#38BDF8' }}>
                        {a.status}
                      </span>
                    </td>
                    <td style={{ padding: '12px 8px' }}>
                      <button
                        onClick={() => setSelectedAssessment(a)}
                        style={{ backgroundColor: '#202330', border: '1px solid #2E3244', color: '#FFFFFF', padding: '6px 12px', borderRadius: '6px', cursor: 'pointer', fontSize: '12px' }}
                      >
                        Examine & Sign
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>

            {/* Assessment Review & Approval Drawer */}
            {selectedAssessment && (
              <div style={{ marginTop: '28px', backgroundColor: '#12141C', border: '1px solid #2E3244', borderRadius: '10px', padding: '20px' }}>
                <h3 style={{ fontSize: '16px', fontWeight: 'bold', marginBottom: '8px', color: '#FF6D00' }}>
                  Professional Review: {selectedAssessment.referenceNumber}
                </h3>
                <p style={{ color: '#94A3B8', fontSize: '13px', marginBottom: '14px' }}>
                  Facility: {selectedAssessment.facilityName} • Assessed by {selectedAssessment.assessorName}
                </p>

                <div style={{ marginBottom: '16px' }}>
                  <label style={{ display: 'block', fontSize: '13px', fontWeight: 'bold', marginBottom: '6px' }}>
                    Occupational Hygienist Evaluation & Recommendation Notes:
                  </label>
                  <textarea
                    value={reviewNotes}
                    onChange={(e) => setReviewNotes(e.target.value)}
                    rows={3}
                    style={{ width: '100%', backgroundColor: '#171922', border: '1px solid #2E3244', borderRadius: '6px', color: '#F8FAFC', padding: '10px', fontSize: '13px' }}
                  />
                </div>

                <div style={{ display: 'flex', gap: '12px' }}>
                  <button
                    onClick={handleApprove}
                    style={{ backgroundColor: '#10B981', color: '#FFFFFF', border: 'none', padding: '10px 20px', borderRadius: '6px', fontWeight: 'bold', cursor: 'pointer' }}
                  >
                    ✓ Approve & Authorize Final Report
                  </button>
                  <button
                    onClick={handleReturn}
                    style={{ backgroundColor: 'transparent', color: '#EF4444', border: '1px solid #EF4444', padding: '10px 20px', borderRadius: '6px', fontWeight: 'bold', cursor: 'pointer' }}
                  >
                    Return for Correction
                  </button>
                </div>
              </div>
            )}
          </div>
        </main>
      </div>
    </div>
  );
}
