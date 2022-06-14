![](app.png)

<h2>Bounding Box Photo App</h2>

<p>
	This is a simple android application that is intended to simplify the process of gathering and labeling image data. The most effective way to train an image recognition or classification algorithm is to utilize a lot of data. Data collection can involve tedious tasks such as manually providing a bounding box for every image. This app skips a few of those steps by allowing users to focus more on capturing images with an easily adjustable bounding box and a simple spinner labeling tool.
</p><br>

<h4>Current Features:</h4>
<ul>
<li>Take photos with an adjustable bounding box.</li>
<li>Add custom labels/classes for images.</li>
<li>Saves information in a local JSON file.</li>
<li>Can export images to a zip file for easy transport.</li>
</ul><br>

<h2>Usage</h2>

<p>
	The design of this application was intended to be familiar with other photo capture apps. The lower section of the screen consists of various buttons such as image capture and label editing.
</p><br>

<h4>Main Screen</h4>
<table>
	<tr>
	<td>Bounding Box</td>
	<td>Adjusted with an upper-left and lower-right button.</td>
	</tr>
	<tr>
	<td>Label Spinner</td>
	<td>Add labels with the "+" button to the right of the spinner. Labels can be removed with the  "-" button.</td>
	</tr>
	<tr>
	<td>Take Photo</td>
	<td>The giant button in the middle is used to capture images.</td>
	</tr>
	<tr>
	<td>Image Viewer</td>
	<td>Activity that shows the user their current images.</td>
	</tr>
	<tr>
	<td>Settings</td>
	<td>The gear to the bottom right provides the user with various options.</td>
	</tr>
</table><br>

<h4>Settings</h4>
<table>
	<tr>
	<td>Change Save Location</td>
	<td>Sets the output direction for the "Zip Files" function.</td>
	</tr>
	<tr>
	<td>Cleanup JSON File</td>
	<td>Erases the data for any removed image.</td>
	</tr>
	<tr>
	<td>Renew JSON File</td>
	<td>Recreates the local JSON file.</td>
	</tr>
	<tr>
	<td>Zip Files</td>
	<td>Compresses all images in the DCIM directory into a zip file.</td>
	</tr>
	<tr>
	<td>Reset Default Settings</td>
	<td>Restores the default for settings that were altered.</td>
	</tr>
</table>