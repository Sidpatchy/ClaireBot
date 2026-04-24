# Requests Channel

The requests channel for a server is determined via the following steps:
<ul>
<li>Getting a Request Channel:
<ul>
<li><b>Step 1</b>: Check ClaireData API
<ul>
<li><b>Success</b>: Use the configured channel</li>
<li><b>Failure</b>: Move to Step 2</li>
</ul>
</li>
<li><b>Step 2</b>: Search for Default Channel
<ul>
<li>Look for channel named "requests"
<ul>
<li><b>Found</b>: Use this channel</li>
<li><b>Not Found</b>: Request fails</li>
</ul>
</li>
</ul>
</li>
</ul>
</li>
</ul>

If no channel is found, the /request will fail.