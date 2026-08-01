from idlelib.query import Query

from fastapi import FastAPI
from pydantic import BaseModel

app = FastAPI()

profile = {
    'hello' : '关于我',
    'helloSubTitle':'项目，创意'
}

@app.get('/api/profile')
def get_profile():
    return profile


class WeatherRequest(BaseModel):
    """
    BaseModel 提供校验、类型转换、自动 __init__ 等功能，同时它是 FastAPI识别"这是请求体模型"的依据。不继承就只是个普通类，注解不生效，FastAPI 也不会从 body 解析它。
    """
    area:str

@app.post('/api/post')
# def post_weather(area:str) -> dict :  # 表单形式
def post_weather(req:WeatherRequest) -> dict :  # json形式
    area = req.area
    if area =='北京':
        return {
            "area":area,
            "weather": "晴天",
            "temperature":"38℃"
        }
    elif area == '上海':
        return {
            "area": area,
            "weather": "雨天",
            "temperature": "338℃"
        }
    return {
        "area": area,
        "weather": "服务器繁忙，稍后重试",
        "temperature": "服务器繁忙，稍后重试"
    }

from fastapi import Path,Query
from pydantic import Field

class PathValidateReq(BaseModel):
    id:int = Field(5,lt=100,gt=0,description="取值范围必须在0-100之间")
    name:str = Field(...,min_length=1,max_length=5,description="参数字符长度需在1-5之间")

@app.post("/api/validate/{path_var}")
def validate(req:PathValidateReq,path_var:int = Path(...,lt=20,gt=0)):
    return {
        "id":f"id是{req.id}",
        "name": f"name是{req.name}",
        "path_var": f"path_var 是 {path_var}"
    }


from fastapi import HTTPException

@app.get("/api/exception")
def get_exception(text:str):
    if text == 'normal':
        return "normal response"
    else:
        return HTTPException(status_code=403,detail="forbidden")

@app.middleware("http") # 表示拦截 HTTP 请求，还有一个 "websocket" 类型，拦截 WebSocket 连接
async def middleware1(request,call_next):
    """
    类似 AOP 的洋葱模型，顺序则是代码文件自上而下
    """
    print("中间件1 start")
    response = await call_next(request)
    print("中间件1 end")
    return response

@app.middleware("http")
async def middleware2(request,call_next):
    """
    类似 AOP 的洋葱模型，顺序则是代码文件自上而下
    """
    print("中间件2 start")
    response = await call_next(request)
    print("中间件2 end")
    return response

@app.get("/api/middleware")
def middleware():
    print("请求处理")
    return "请求返回"

