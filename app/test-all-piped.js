(async () => {
  try {
    const listReq = await fetch('https://piped-instances.kavin.rocks/');
    let text = await listReq.text();
    text = text.substring(text.indexOf('['));
    let instances = JSON.parse(text).map(i => i.api_url);
    console.log(`Testing ${instances.length} instances...`);
    for (let url of instances) {
      if(!url) continue;
      try {
        let res = await fetch(url + '/streams/YQHsXMglC9A', { signal: AbortSignal.timeout(3000) });
        if (res.status === 200) {
           const jsonText = await res.text();
           if (jsonText.includes('"hls"')) {
               console.log("\nWORKING:", url);
           }
        }
      } catch (e) {}
      process.stdout.write('.');
    }
  } catch(e) {
    console.error(e);
  }
})();
