# Sigmoter
* Sigmoter means **S**ement**i**c **G**uided **Mo**bile **Te**st **R**epair 
* This is my Graduation Design Tool, which is dedicated for automatic mobile test repair. 
* Sigmoter 能处理的测试用例语句包含（取决于Appium Inspector所支持的操作）：
> * `driver.findElementBy...`
>> * `driver.findElementByXpath("value").clear()/click()/sendKeys("value")`
>> * `driver.findElementById("value").clear()/click()/sendKeys("value")`
>> * `driver.findElementByAccessibilityId("value").clear()/click()/sendKeys("value")`
> * `driver.navigate()`
>> * 回退：`driver.navigate().back()`
>> * 前进：`driver.navigate().forward()`
>> * 刷新：`driver.navigate().refresh()`
> * `new TouchAction(driver)`
>> * 滑动屏幕：`new TouchAction(driver).press(PointOption.point(x1,y1)).moveTo(PointOption.point(x2,y2)).release().perform()`
>> * PS: 不考虑`new TouchAction(driver).tap(x,y).perform()`，因为元素定位通过`findElement`实现，而不是坐标点击