const instances = [
    "https://pipedapi.kavin.rocks/",
    "https://pipedapi.us.projectsegfau.lt/",
    "https://pipedapi.in.projectsegfau.lt/",
    "https://pipedapi.eu.projectsegfau.lt/",
    "https://pipedapi.asia.projectsegfau.lt/",
    "https://pi.pivpn.moe/",
    "https://pipedapi.syncpundit.io/",
    "https://pipedapi.adminforge.de/",
    "https://api.piped.privacydev.net/",
    "https://piped-api.lunar.icu/",
    "https://api.piped.mint.lgbt/",
    "https://pipedapi.smnz.de/",
    "https://pipedapi.tokhmi.xyz/",
    "https://piped-api.privacy.com.de/",
    "https://pipedapi.ytmnd.com/",
    "https://api.piped.bz/",
    "https://pipedapi.1337.cx/",
    "https://api-piped.mha.fi/"
];

async function check() {
    for (const inst of instances) {
        try {
            const controller = new AbortController();
            const timeoutId = setTimeout(() => controller.abort(), 3000);
            const res = await fetch(`${inst}trending?region=US`, {
                headers: { 'User-Agent': 'Mozilla/5.0' },
                signal: controller.signal
            });
            clearTimeout(timeoutId);
            const text = await res.text();
            console.log(`${inst} -> ${res.status} ${res.headers.get('content-type')} | Body: ${text.slice(0, 50)}`);
        } catch (e) {
            console.log(`${inst} -> ${e.name}: ${e.message}`);
        }
    }
}
check();
