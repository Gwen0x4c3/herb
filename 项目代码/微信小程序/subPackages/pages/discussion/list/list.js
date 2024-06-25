const app = getApp()
import { parseTime } from '../../../../utils/util.js'

Page({
  data: {
    discussions: [],
    page: 1,
    totalPage: 1
  },
  onLoad(options) {
    this.getDiscussions()
  },
  getDiscussions() {
    app.request({
      url: '/user/discussion/list',
      data: {
        page: this.data.page
      }
    }).then(res => {
      let list = res.data.page.list
      for (let item of list) {
        item.timeStr = parseTime(item.createTime)
      }
      this.setData({
        discussions: list,
        page: res.data.page.currPage,
        totalPage: res.data.page.totalPage
      })
    })
  },
  toDiscussionPage(e) {
    let id = e.currentTarget.dataset.id
    wx.navigateTo({
      url: '../../article/show/show?type=1&id=' + id,
    })
  },
  onReachBottom() {
    let page = this.data.page
    if (page >= this.data.totalPage) {
      wx.showToast({
        title: '已经到最底了'
      })
      return
    }
    this.setData({
      page: page + 1
    })
    this.getDiscussions()
  }
})