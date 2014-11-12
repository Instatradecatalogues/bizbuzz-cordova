function MFSConstructor(){
	this.fs = undefined;
	this.rootDirEntry = undefined;
	this.debug = true;
	this.rootDirName = "InstaTrade";
}

/**
 * Returns the File System of the mobile device. It adds filesystem object to the successArgs
 * and can be accessed as successArgs.fs. 
 * 
 * ARGS:
 * success: callback for the success
 * successArgs: Arguments to be passed to the success function callback
 * fail: callback for the fail
 */
MFSConstructor.prototype.getFS = function (args){
	var that = this;
	function success(fs){
		that.fs = fs;
		if(args.success !== undefined){
			if(args.successArgs === undefined)
				args.successArgs = new Object();
			args.successArgs.fs = fs;
			args.success(args.successArgs);
		}
	}
	
	window.requestFileSystem(LocalFileSystem.PERSISTENT, 0, success, args.fail);
}

/**
 * Returns the Root directory of the mobile device. It adds DirectoryEntry object of the root
 * to the successArgs and can be accessed as successArgs.rootDirEntry.
 * 
 * ARGS
 * success: callback for the success
 * successArgs: Arguments to be passed to the success function callback
 * fail: callback for the fail
 */
MFSConstructor.prototype.getRootDirectory = function (args){
	var fs = this.fs;
	var that = this;
	if(fs === undefined){
		return;
	}
	function success(rootDirEntry){
		that.rootDirEntry = rootDirEntry;
		if(args.success !== undefined){
			if(args.successArgs === undefined)
				args.successArgs = new Object();
			args.successArgs.rootDirEntry = rootDirEntry;
			args.success(args.successArgs);
		}
	}
	
	fs.root.getDirectory(this.rootDirName, {create: true, exclusive: false}, success, args.fail);
}

/**
 * This function give access to a directory. It assumes that app root dir object has been created.
 * DirEntry object is accessible in success callback using successArgs.dirEntry
 * 
 * ARGS
 * create: Do you want to create directory if doesn't exist; default=true
 * success: callback on directory access
 * successArgs: arguments for success callback
 * fail: callback on directory access fail
 * dirName: name of the dir
 * currentDirEntry: DirectoryEntry of the current directory; Default is appRootDir
 */
MFSConstructor.prototype.getDirectoryEntry = function(args){
	var create = true;
	var currentDirEntry = this.rootDirEntry;
	var dirName;
	if(args.create !== undefined)
		create = args.create;
	if(args.dirName !== undefined)
		dirName = args.dirName;
	if(args.currentDirEntry !== undefined)
		currentDirEntry = args.currentDirEntry;
	function success(dirEntry){
		if(args.success !== undefined){
			if(args.successArgs === undefined)
				args.successArgs = new Object();
			args.successArgs.dirEntry = dirEntry;
			args.success(args.successArgs);
		}
	}
	currentDirEntry.getDirectory(dirName, {create:create, exclusive: false}, success, args.fail);
}

/**
 * This function give access to a File. It assumes that app root dir object has been created.
 * FileEntry object is accessible in success callback using successArgs.fileEntry
 * 
 * ARGS
 * create: Do you want to create directory if doesn't exist
 * success: callback on directory access
 * successArgs: arguments for success callback
 * fail: callback on directory access fail
 * fileName: name of the dir
 * currentDirEntry: DirectoryEntry of the current directory; Default is appRootDir
 */
MFSConstructor.prototype.getFileEntry = function(args){
	var create = true;
	var currentDirEntry = this.rootDirEntry;
	if(args.create !== undefined)
		create = args.create;
	if(args.fileName !== undefined)
		fileName = args.fileName;
	else
		return "Filename is mandatory";
	if(args.currentDirEntry !== undefined){
		currentDirEntry = args.currentDirEntry;
	}
	function success(fileEntry){
		if(args.success !== undefined){
			if(args.successArgs === undefined)
				args.successArgs = new Object();
			args.successArgs.fileEntry = fileEntry;
			args.success(args.successArgs);
		}
	}
	currentDirEntry.getFile(fileName, {create:create, exclusive: false}, success, args.fail);
}

/**
 * ARGS
 * fileEntry: file entry of which file object needs to be returned
 * success: callback function. It gives file object as argument. file object is retrieved as args.file
 * successArgs
 * fail: fail callback function.
 */
MFSConstructor.prototype.getFile = function(args){
	var fileEntry = args.fileEntry;
	function success(file){
		if(args.success !== undefined){
			if(args.successArgs === undefined)
				args.successArgs = new Object();
			args.successArgs.file = file;
			args.success(args.successArgs);
		}
	}
	fileEntry.file(success, args.fail);
}

/**
 * ARGS
 * file: file object which needs to be read
 * success: callback to be executed when read is finished. Data of file can be accessed in the callback using args.data
 * successArgs: arguments of success callback
 */
MFSConstructor.prototype.readAsText=function(args){
	var reader = new FileReader();
	reader.onload = function(evt){
		if(args.success !== undefined){
			if(args.successArgs === undefined)
				args.successArgs = new Object();
			args.successArgs.data = evt.target.result;
			args.success(args.successArgs);
		}
	}
	reader.readAsText(args.file);
}

/**
 * ARGS
 * file: file object which needs to be read
 * onload: callback to be executed when read is finished. Data of file can be accessed in the callback using first argument
 * successArgs: arguments of success callback
 */
MFSConstructor.prototype.readAsDataUrl=function(args){
	var reader = new FileReader();
	reader.onload = function(evt){
		if(args.success !== undefined){
			if(args.successArgs === undefined)
				args.successArgs = new Object();
			args.successArgs.data = evt.target.result;
			args.success(args.successArgs);
		}
	}
	reader.readAsDataUrl(args.file);
}

/**
 * ARGS
 * fileEntry: fileEntry of which writer is needed
 * success: callback when writer is created succefully
 * successArgs: writer can be obtained as successArgs.writer
 * fail:
 */
MFSConstructor.prototype.getWriter=function(args){
	var fileEntry = args.fileEntry;
	function success(writer){
		if(args.success !== undefined){
			if(args.successArgs === undefined)
				args.successArgs = new Object();
			args.successArgs.writer = writer;
			args.success(args.successArgs);
		}
	}
	fileEntry.createWriter(success, args.fail);
}

/**
 * ARGS
 * writer: writer using which file needs to be written
 * onwriteend: Notifiying callback about file being successfuly written
 * data: data that needs to be written
 * 
 */
MFSConstructor.prototype.write=function(args){
	if(args.writer === undefined)
		return;
	if(args.data === undefined)
		return;
	args.writer.onwriteend = function(evt){
		if(args.onwriteend !== undefined)
			args.onwriteend(evt);
	};
	args.writer.write(args.data);
}

/**
 * This function returns the data of a file as text when supplied with a path to the file in the form of a 
 * list of directories
 * 
 * ARGS
 * fileName:
 * path: A list of folders (in the form of list) from app home to the file parent folder. Empty means file is in app root.
 * success: callback for successful data read. Data is returned as and arguement and can be accessed as args.data
 * successArgs:
 * fail:
 */
MFSConstructor.prototype.readAFileAsTextShortcut=function(args){
	function dataRead(dataArgs){
		var data = dataArgs.data;
		if(args.success !== undefined){
			if(args.successArgs === undefined)
				args.successArgs = new Object();
			args.successArgs.data = data;
			args.success(args.successArgs);
		}
	}
	function fileSuccess(fileSuccessArgs){
		MobileFileSystem.readAsText({success: dataRead, file:fileSuccessArgs.file, fail:args.fail});
	}
	function fileEntrySuccess(fileEntryArgs){
		MobileFileSystem.getFile({fileEntry: fileEntryArgs.fileEntry, success: fileSuccess, fail:args.fail});
	}
	function dirSuccess(dirEntryArgs){
		MobileFileSystem.getFileEntry({create:true, success: fileEntrySuccess, currentDirEntry: dirEntryArgs.dirEntry, fileName: args.fileName, fail:args.fail});
	}
	MobileFileSystem.getDirEntryShortcut({path: args.path, success:dirSuccess, fail: args.fail});
}

/**
 * This function returns the data of a file as data url when supplied with a path to the file in the form of a 
 * list of directories
 * 
 * ARGS
 * fileName:
 * path: A list of folders (in the form of list) from app home to the file parent folder. Empty means file is in app root.
 * success: callback for successful data read. Data is returned as and arguement and can be accessed as args.data
 * successArgs:
 * fail:
 */
MFSConstructor.prototype.readAFileAsDataUrlShortcut=function(args){
	function dataRead(dataArgs){
		var data = dataArgs.data;
		if(args.success !== undefined){
			if(args.successArgs === undefined)
				args.successArgs = new Object();
			args.successArgs.data = data;
			args.success(args.successArgs);
		}
	}
	function fileSuccess(fileSuccessArgs){
		MobileFileSystem.readAsDataUrl({success: dataRead, file:fileSuccessArgs.file, fail:args.fail});
	}
	function fileEntrySuccess(fileEntryArgs){
		MobileFileSystem.getFile({fileEntry: fileEntryArgs.fileEntry, success: fileSuccess, fail:args.fail});
	}
	function dirSuccess(dirEntryArgs){
		MobileFileSystem.getFileEntry({create:true, success: fileEntrySuccess, currentDirEntry: dirEntryArgs.dirEntry, fileName: args.fileName, fail:args.fail});
	}
	MobileFileSystem.getDirEntryShortcut({path: args.path, success:dirSuccess, fail: args.fail});
}

/**
 * This function write the data to a file when supplied with a path to the file in the form of a 
 * list of directories
 * 
 * ARGS
 * fileName:
 * data: Data that needs to be written
 * path: A list of folders (in the form of list) from app home to the file parent folder. Empty means file is in app root.
 * onwriteend: notifying when the write is complete
 * fail:
 */
MFSConstructor.prototype.writeInAFileShortcut=function(args){
	function writerSuccess(writerArgs){
		MobileFileSystem.write({writer: writerArgs.writer, data: args.data, onwriteend: args.onwriteend});
	}
	function fileEntrySuccess(fileEntryArgs){
		MobileFileSystem.getWriter({fileEntry: fileEntryArgs.fileEntry, success: writerSuccess, fail:args.fail});
	}
	function dirSuccess(dirEntryArgs){
		MobileFileSystem.getFileEntry({create:true, success: fileEntrySuccess, currentDirEntry: dirEntryArgs.dirEntry, fileName: args.fileName, fail:args.fail});
	}
	MobileFileSystem.getDirEntryShortcut({path: args.path, success:dirSuccess, fail: args.fail});
}

/**
 * ARGS
 * path: A list of folders (in the form of list) from app home to the file parent folder. Empty means file is in app root.
 * CurrentDirEntry:
 * success:
 * successArgs
 * fail:
 */
MFSConstructor.prototype.getDirEntryShortcut=function(args){
	var that = this;
	function success(dirEntryArgs){
		MobileFileSystem.getDirEntryShortcut({path: args.path, currentDirEntry:dirEntryArgs.dirEntry, success:args.success, successArgs: args.successArgs, fail: args.fail});
	}
	
	function rootDirSuccess(rootDirSuccessArgs){
		that.rootDirEntry = rootDirSuccessArgs.rootDirEntry;
		success(rootDirSuccessArgs);
	}
	function fsSuccess(fsSuccessArgs){
		that.fs = fsSuccessArgs.fs;
		if(that.rootDirEntry === undefined)
			MobileFileSystem.getRootDirectory({success:rootDirSuccess, fail:args.fail});
	}
	if(that.fs === undefined){
		MobileFileSystem.getFS({success: fsSuccess, fail: args.fail});
	}
	else if(that.rootDirEntry === undefined){
		MobileFileSystem.getRootDirectory({success:rootDirSuccess, fail:args.fail});
	}
	else{
		if(args.currentDirEntry===undefined)
			args.currentDirEntry = that.rootDirEntry;
		if(args.path.length == 0){
			if(args.successArgs === undefined)
				args.successArgs = new Object();
			args.successArgs.dirEntry = args.currentDirEntry;
			args.success(args.successArgs);
		}
		else{
			MobileFileSystem.getDirectoryEntry({success: success, successArgs: args.successArgs, fail: args.fail, dirName: args.path.shift()});
		}
	}
}

var MobileFileSystem = new MFSConstructor();
