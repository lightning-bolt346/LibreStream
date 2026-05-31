(async () => {
  try {
    const listReq = await fetch('https://api.invidious.io/instances.json');
    let data = await listReq.json();
    let urls = data.map(i => i[1].uri);
    console.log(`Testing ${urls.length} instances...`);
    for (let u of urls) {
      if(!u.startsWith('https')) continue;
      try {
        let res = await fetch(u + '/api/v1/videos/YQHsXMglC9A', { signal: AbortSignal.timeout(10000) });
        if (res.status === 200) {
           const jsonText = await res.text();
           if (jsonText.includes('"formatStreams"')) {
               console.log("\nWORKING:", u);
           }
        }
      } catch (e) {}
      process.stdout.write('.');
    }
  } catch(e) {
    console.error(e);
  }
})();
