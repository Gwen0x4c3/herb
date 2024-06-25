import {request} from '/utils/request.js'

App({
  request(params) {
    return request(params)
  },
  onLaunch() {
    console.log('AUTO LOGIN...')
    let ticket = wx.getStorageSync('TICKET');
    console.log(ticket)
    // if (ticket) {
    //   return
    // } 
    wx.showLoading({
      title: '加载中',
    })
    // 登录
    wx.login({
      success: res => {
        console.log('login', res)
        if (res.code) {
          wx.request({
            url: 'http://iherb.com/wx/login',
            data: {
              code: res.code,
              userTicket: ticket
            },
            success: res => {
              ticket = res.data.data.ticket
              if (ticket) {
                wx.setStorageSync('TICKET', ticket)
              }
              wx.hideLoading({
                success: (res) => {},
              })
            }
          })
        }
      }
    })
  },
  globalData: {
    userInfo: null,
    baseUrl: 'http://iherb.com'
  }
})
