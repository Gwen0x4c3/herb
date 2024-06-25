const app = getApp()
import {parseTime} from '../../utils/util.js'

Page({
  data: {
    pickerArray: ['找中药', '找药方', '找文章'],
    searchType: 0,
    keyword: '',
    hints: [],
    hintsVisible: false,
    searchPanelVisible: false,
    articles: [],
    excludeIds: []
  },
  onLoad() {
    console.log(parseTime)
    this.getArticles()
  },
  getArticles() {
    app.request({
      url: '/user/article/recommend',
      data: {
        excludeIds: this.data.excludeIds + ''
      }
    }).then(res => {
      let list = res.data.list
      let excludeIds = this.data.excludeIds;
      for (let item of list) {
        excludeIds.push(item.id)
        item.timeStr = parseTime(item.createTime)
        let viewCount = item.viewCount
        if (viewCount > 10000) {
          viewCount = Math.floor(viewCount / 10000) + 'w+'
        }
        item.viewCount = viewCount
      }
      this.setData({
        articles: [...this.data.articles, ...list],
        excludeIds: excludeIds
      })
    })
  },
  handleSearchConfirm(e) {
    wx.showToast({
      title: '正在搜索' + e.detail.value,
      icon: 'none'
    })
  },
  handleSearchInput(e) {
    let value = e.detail.value
    this.setData({
      keyword: value
    })
    if (value == null || value.length == 0) {
      this.setData({
        hints: [],
        hintsVisible: false
      })
      return;
    } 
    this.searchForHints(value, this.data.searchType)
  },
  handleSearchBlur() {
    this.setData({
      hintsVisible: false
    })
  },
  handleSearchFocus() {
    if (this.data.hints != null && this.data.hints.length != 0) {
      this.setData({
        hintsVisible: true
      })
    }
  },
  clearKeyword() {
    this.setData({
      keyword: '',
      hints: []
    })
  },
  searchForHints(keyword, type) {
    //TODO 根据当前关键词搜索可能性高的选项
    wx.request({
      url: 'http://localhost:8002/searchForHints',
      data: {
        keyword: this.data.keyword,
        type: this.data.searchType
      },
      success: res => {
        console.log(res)
        if (this.data.keyword) {
          this.setData({
            hints: res.data.list,
            hintsVisible: true
          }) 
        }
      },
      fail: err => {
        console.log(err)
        wx.showToast({
          title: '网络延迟',
        })
      }
    })
  },
  handleHintClick(e) {
    let keyword = e.target.dataset.keyword
    this.setData({
      keyword: keyword,
      hints: []
    })
    this.search(keyword, this.data.searchType)
  },
  search(keyword, type) {

  },
  navigate(e) {
    console.log(e)
    let target = e.currentTarget.dataset.target
    let url = '/subPackages/pages'
    if (target == 0) {
      url += '/cate/cate'
    } else if (target == 1) {
      url += '/discussion/list/list'
    } else if (target == 2) {
      url += '/article/edit/edit?type=0'
    }
    wx.navigateTo({
      url: url
    })
  },
  toArticlePage(e) {
    console.log(e)
    let id = e.currentTarget.dataset.id
    wx.navigateTo({
      url: '/subPackages/pages/article/show/show?type=0&id=' + id
    })
  },
  onReachBottom() {
    wx.showLoading({
      title: '加载中',
    })
    wx.hideLoading({
      success: (res) => {
        this.getArticles()
      },
    })
  }
}) 
