var MobileFileSystemTest = function() {
	var fileEntry;
	var that=this;
	function onDeviceReady(){
		alert("in");
		//MobileFileSystem.readAFileAsTextShortcut({path: ["config"], fileName: "properties.txt", success: dataRead, fail:fail});
		var testObj = new Object();
		testObj.username = "hello";
		testObj.password = "pass";
		var testObjString = JSON.stringify(testObj);
		MobileFileSystem.writeInAFileShortcut({path: ["config"], fileName: "properties.txt", fail:fail, data: testObjString, onwriteend: onwriteend});
		//MobileFileSystem.getFS({success: fsSuccess});
		//MobileFileSystem.getFile({fileEntry: fileEntry, success: fileSuccess});
	}
	
	function onwriteend(){
		MobileFileSystem.readAFileAsTextShortcut({path: ["config"], fileName: "properties.txt", success: dataRead, fail:fail});
	}
	
	function fail(error){
	}

	function fsSuccess(args){
		var fs = args.fs;
		MobileFileSystem.getRootDirectory({success:rootDirSuccess});
	}

	function rootDirSuccess(args){
		var rootDirEntry = args.rootDirEntry;
		MobileFileSystem.getDirectoryEntry({create:true, success: dirSuccess, currentDirEntry: rootDirEntry, dirName: "config"});
	}

	function dirSuccess(args){
		var currentDirEntry = args.dirEntry;
		MobileFileSystem.getFileEntry({create:true, success: fileEntrySuccess, currentDirEntry: currentDirEntry, fileName: "properties.txt"});
	}

	function fileEntrySuccess(args){
		fileEntry = args.fileEntry;
		//MobileFileSystem.getWriter({fileEntry: fileEntry, success: writerSuccess});
		MobileFileSystem.getFile({fileEntry: fileEntry, success: fileSuccess});
	}

	function writerSuccess(args){
		var writer = args.writer;
		var testObj = new Object();
		testObj.username = "user";
		testObj.password = "pass";
		var testObjString = JSON.stringify(testObj);
		MobileFileSystem.write({writer: writer, data: testObjString});
	}

	function fileSuccess(args){
		var file = args.file;
		MobileFileSystem.readAsText({success: dataRead, file:file});
	}

	function dataRead(args){
		var data = args.data;
		var obj = JSON.parse(data);
		alert(obj.username);
	}

	function loadScript(url, callback)
	{
		// Adding the script tag to the head as suggested before
		var head = document.getElementsByTagName('head')[0];
		var script = document.createElement('script');
		script.type = 'text/javascript';
		script.src = url;

		// Then bind the event to the callback function.
		// There are several events for cross browser compatibility.
		script.onreadystatechange = callback;
		script.onload = callback;

		// Fire the loading
		head.appendChild(script);
	}
	document.addEventListener("deviceready", onDeviceReady, false);
}