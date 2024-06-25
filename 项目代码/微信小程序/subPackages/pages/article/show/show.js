const app = getApp()

Page({
  data: {
    id: '',
    type: 0,
    page: 1,
    totalPage: 1,
    data: {},
    comments: [],
    count: 0,
    sort: 0, //0-正序 1-倒序
    inputValue: '',
    inputFocus: false,
    bottom: 0
  },
  onLoad(options) {
    let id = options.id
    let type = options.type
    this.setData({
      id: id,
      type: type
    })
    if (type == 0) {
      this.getArticle(id)
    } else {
      this.getDiscussion(id)
    }
    this.getComments()
  },
  getArticle(id) {
    app.request({
      url: '/user/article/' + id
    }).then(res => {
      console.log(res)
      let article = res.data.article
      let content = article.content.replace(/\<img/gi, '<img class="article-image"')
        .replace(/\<p/gi, '<p class="article-paragraph"')
      article.content = content
      this.setData({
        data: article
      })
    })
  },
  getDiscussion(id) {
    app.request({
      url: '/user/discussion/' + id
    }).then(res => {
      console.log(res)
      let discussion = res.data.discussion
      let content = discussion.content.replace(/\<img/gi, '<img class="article-image"')
        .replace(/\<p/gi, '<p class="article-paragraph"')
      discussion.content = content
      this.setData({
        data: discussion
      })
    })
  },
  getComments() {
    app.request({
      url: '/user/comment/list',
      data: {
        page: this.data.page,
        id: this.data.id,
        type: this.data.type,
        sort: this.data.sort
      }
    }).then(res => {
      console.log(res)
      let page = res.data.page
      let comments = res.data.page.list
      for (let comment of comments) {
        comment.timeStr = this.parseTime(comment.createTime)
      }
      this.setData({
        page: page.currPage,
        totalPage: page.totalPage,
        comments: [...this.data.comments, ...page.list],
        count: page.totalCount
      })
    })
  },
  handleSortChange(e) {
    let sort = e.target.dataset.sort
    this.setData({
      sort: sort,
      page: 1,
      comments: []
    })
    this.getComments()
  },
  handleReachBottom() {
    console.log('到底了哦')
    let page = this.data.page
    let totalPage = this.data.totalPage
    if (page >= totalPage) {
      return
    }
    this.setData({
      page: page + 1
    })
    wx.showLoading({
      title: '加载评论中',
    })
    wx.hideLoading({
      success: (res) => {
        this.getComments()
      },
    })
  },
  handleFocus() {
    this.setData({
      inputFocus: true
    })
  },
  handleBlur() {
    this.setData({
      inputFocus: false
    })
  },
  handleInput(e) {
    console.log('input', e)
  },
  handleKeyboardChange(e) {
    console.log('keyboard', e)
    this.setData({
      bottom: e.detail.height
    })
  },
  sendComment() {
    let comment = {
      targetId: this.data.id,
      content: this.data.inputValue,
      type: this.data.type
    }
    app.request({
      url: '/user/comment/save',
      method: 'POST',
      data: comment
    }).then(res => {
      this.setData({
        page: 1,
        comments: [],
        inputValue: ''
      })
      this.getComments()
      wx.showToast({
        title: '发送成功'
      })
    }) 
  },
  parseTime(date) { //获取js 时间戳
    date = new Date(date).getTime()
    var time = new Date().getTime(); //去掉 js 时间戳后三位，与时间戳保持一致
    time = parseInt((time - date) / 1000); //存储转换值
    var s;
    if (time < 60 * 10) { //十分钟内
      return '刚刚';
    } else if (time < 60 * 60) { //超过十分钟少于1小时
      s = Math.floor(time / 60);
      return s + "分钟前";
    } else if (time < 60 * 60 * 24) {
      //超过1小时少于24小时
      s = Math.floor(time / 60 / 60);
      return s + "小时前";
    } else if (time < 60 * 60 * 24 * 3)  {
      //超过1天少于3天内
      s = Math.floor(time / 60 / 60 / 24);
      return s + "天前";
    } else {
      //超过3天
      date = new Date(date);
      return date.getFullYear() + "-" + (date.getMonth() + 1) + "-" + date.getDate();
    }
  }
})