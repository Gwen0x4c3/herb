const app = getApp()

Page({
  data: {
    type: 0,
    title: '',
    content: ''
  },
  onLoad(options) {
    this.setData({
      type: options.type
    })
  },
  handleInputChange(e) {
    console.log(e)
    this.setData({
      content: e.detail.html
    })
  },
  submit() {
    let content = this.data.content.replaceAll(/<p .*?>/g, '')
    let post = {
      title: this.data.title,
      content: content
    }
    console.log(post)
    console.log(post.content)
    let url = '/user/$1/save'
    if (this.data.type == 0) {
      url = url.replace('$1', 'article')
    } else {
      url = url.replace('$1', 'discussion')
    }
    app.request({
      url: url,
      method: 'POST', 
      data: post
    }).then(res => {
      console.log(res)
    })
  }
})