export function encodeBase64(input: string): string {
	var output = "";
	var chr1: number, chr2: number, chr3: number, enc1: number, enc2: number, enc3: number, enc4: number;
	var i = 0;

	input = utf8Encode(input);

	while (i < input.length) {
		chr1 = input.charCodeAt(i++);
		chr2 = input.charCodeAt(i++);
		chr3 = input.charCodeAt(i++);

		enc1 = chr1 >> 2;
		enc2 = ((chr1 & 3) << 4) | (chr2 >> 4);
		enc3 = ((chr2 & 15) << 2) | (chr3 >> 6);
		enc4 = chr3 & 63;

		if (isNaN(chr2)) {
			enc3 = enc4 = 64;
		} else if (isNaN(chr3)) {
			enc4 = 64;
		}

		output = output +
			keyStr.charAt(enc1) + keyStr.charAt(enc2) +
			keyStr.charAt(enc3) + keyStr.charAt(enc4);

	}
	return output;
}

export function encodeAsFilename(input: string): string {
	let encoded = encodeBase64(input);
	return encoded.replace(/\//g, '_').replace(/\+/g, '-');
}

function utf8Encode(input: string): string {
	input = input.replace(/\r\n/g, "\n");
	var utftext = "";

	for (var n = 0; n < input.length; n++) {

		var c = input.charCodeAt(n);

		if (c < 128) {
			utftext += String.fromCharCode(c);
		} else if ((c > 127) && (c < 2048)) {
			utftext += String.fromCharCode((c >> 6) | 192);
			utftext += String.fromCharCode((c & 63) | 128);
		} else {
			utftext += String.fromCharCode((c >> 12) | 224);
			utftext += String.fromCharCode(((c >> 6) & 63) | 128);
			utftext += String.fromCharCode((c & 63) | 128);
		}
	}
	return utftext;
}

var keyStr: string = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";
