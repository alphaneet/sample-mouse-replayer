require 'test_helper'

class ReplaysControllerTest < ActionController::TestCase
  test "should get index" do
    get :index
    assert_response :success
    assert_not_nil assigns(:replays)
  end

  test "should get new" do
    get :new
    assert_response :success
  end

  test "should create replay" do
    assert_difference('Replay.count') do
      post :create, :replay => { }
    end

    assert_redirected_to replay_path(assigns(:replay))
  end

  test "should show replay" do
    get :show, :id => replays(:one).to_param
    assert_response :success
  end

  test "should get edit" do
    get :edit, :id => replays(:one).to_param
    assert_response :success
  end

  test "should update replay" do
    put :update, :id => replays(:one).to_param, :replay => { }
    assert_redirected_to replay_path(assigns(:replay))
  end

  test "should destroy replay" do
    assert_difference('Replay.count', -1) do
      delete :destroy, :id => replays(:one).to_param
    end

    assert_redirected_to replays_path
  end
end
