// components/pagination/pagination.js
Component({
  /**
   * 组件的属性列表
   */
  properties: {
    currentPage: { //当前页码
      type: Number,
      value: 1
    },
    totalPage: {
      type: Number
    }
  },
 
  /**
   * 组件的初始数据
   */
  data: {
    index: 1,
    total: 10,
    pageMask: false,
    prevBtnDis: true,
    nextBtnDis: false
  },
 
  /**
   * 组件的方法列表
   */
  lifetimes: {
     // 在组件实例进入页面节点树时执行
    attached: function () {
      console.log(this.properties)
      this.setData({
        index: this.properties.currentPage,
        total: this.properties.totalPage
      })
    }
    
  },
  methods: {

    // 设置异步请求之后的页面、总记录数
    setPage(index, total){
      console.log('set page:' + index + ' ' + total)
      this.setData({
        index,
        total
      })
    },

    //每次改变页码就调用该函数
    triggerParent: function () {

      // 通知父组件当前加载的页数

      // 自定义组件向父组件传值 
        const option = {
          currentPage: this.data.index
        };

      // pagingChange 自定义名称事件，父组件中使用
      this.triggerEvent('pagingChange', option)
     
    },
    //开启页码弹窗
    showPagePopUp: function () {
      this.setData({
        pageMask: true
      })
    },
    //关闭页码弹窗
    hidePagePopUp: function () {
      this.setData({
        pageMask: false
      })
    },
    //更改页码点击事件
    onChangePage: function (e) {
      console.log("更改页码事件：",e);
      this.setData({
        pageMask: false,
        index: e.currentTarget.dataset.index //点击的页数
      })

      // 先判断当前页数，是否需要更新disabled的状态
      this.updateBtnDis();

      this.triggerParent();
    
    },
    //上一页点击事件
    prevPage: function () {
      if(this.data.index <= 1) return; 
      let num = this.data.index - 1;
      this.setData({
        index: num
      })
      this.triggerParent();
      // 更新按钮状态
      this.updateBtnDis();
    },
    //下一页点击事件
    nextPage: function () {
      if(this.data.index >= this.data.total) return; 
      let num = this.data.index + 1;
      this.setData({
        index: num
      })

      this.triggerParent();

      // 更新按钮状态
      this.updateBtnDis();
    },

    //判断按钮是否为disabled
    updateBtnDis: function () {

      let index = this.data.index;
      let total = this.data.total;

      if(index == total && index == 1){ // 都为起始页和总页数都为1
        this.setData({
          nextBtnDis: true,
          prevBtnDis: true
        })
      }else if (index == total) { // 最后一页
        this.setData({
          nextBtnDis: true
        })
      } else if (index == 1){ // 第一页
        this.setData({
          prevBtnDis: true
        })
      }else{
        this.setData({
          prevBtnDis: false
        })
        this.setData({
          nextBtnDis: false
        })
      }
    }
  }
})
