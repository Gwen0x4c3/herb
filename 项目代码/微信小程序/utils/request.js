const baseUrl = 'http://iherb.com'
// const baseUrl = 'http://192.168.12.1'

export const request = (params) => {
	let ticket = wx.getStorageSync('TICKET')
	let url = baseUrl
	if (params.url.startsWith('http')) {
		url = params.url
	} else {
		url += params.url
	}
	if (params.header) {
		params.header = {...params.header, TICKET: ticket}
	} else {
		params.header = {TICKET: ticket}
	}
	return new Promise((resolve,reject)=>{
		wx.request({
			...params,
			url: url,
			success(result) {
				if (result.data.code == 200) 
					resolve(result.data);
				// else
				// 	reject(result.data)
			},
			fail(err) {
				reject(err);
			}
		});
	})
}
