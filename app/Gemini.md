Paste this into the index.html inside app/src/main/assets/
Problem: Android WebView renders elements invisible because animation: greet-slide ... both sets opacity:0 before the animation delay fires, and WebView doesn't always resolve it back. The loader also has a race condition.
Make these 4 targeted changes:

Change 1 — Fix the greet-slide keyframe (find & replace)
Find:
css@keyframes greet-slide {
from { opacity:0; transform:translateY(18px) scale(.97); }
to   { opacity:1; transform:translateY(0) scale(1); }
}
Replace with:
css@keyframes greet-slide {
from { opacity:1; transform:translateY(12px); }
to   { opacity:1; transform:translateY(0); }
}

Change 2 — Fix greeting-sub opacity (find & replace)
Find:
css.greeting-sub { font-size:.69rem; ... opacity:0; animation-fill-mode:forwards; }
Replace the opacity:0 with opacity:1 in that rule.

Change 3 — Add a WebView safety override (paste just before </style>)
css/* ── WEBVIEW VISIBILITY FIX ── */
.screen.active,
.screen.active .main,
.screen.active .main > *,
.balance-hero, .stats-grid, .action-row,
.chart-card, .seasons-card, .ref-card, .notice-strip,
.greeting-wrap, .page-header {
opacity: 1 !important;
visibility: visible !important;
}

Change 4 — Fix the loader (find & replace in <script>)
Find:
jswindow.addEventListener('load',()=>{
setTimeout(()=>{
const l=document.getElementById('loader');
l.style.transition='opacity .4s ease';
l.style.opacity='0';
setTimeout(()=>l.style.display='none',400);
setTimeout(()=>{
document.getElementById('prog-fill').style.width='67.3%';
},600);
},1400);
});
Replace with:
jsfunction dismissLoader(){
const l=document.getElementById('loader');
if(!l||l.style.display==='none') return;
l.style.transition='opacity .4s ease';
l.style.opacity='0';
setTimeout(()=>{ l.style.display='none'; },400);
setTimeout(()=>{
const p=document.getElementById('prog-fill');
if(p) p.style.width='67.3%';
},300);
}
window.addEventListener('load', ()=>setTimeout(dismissLoader, 800));
// Fallback: force dismiss after 3s no matter what
setTimeout(dismissLoader, 3000);

After these 4 changes, save index.html, rebuild the APK (Build → Rebuild Project in Android Studio), and install it. The dashboard content will render immediately.