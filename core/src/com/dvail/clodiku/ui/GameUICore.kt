package com.dvail.clodiku.ui

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.dvail.clodiku.StartScreen
import com.dvail.clodiku.entities.CompMapper
import com.dvail.clodiku.entities.Comps
import com.dvail.clodiku.events.*
import com.dvail.clodiku.util.BoundKeys
import com.dvail.clodiku.util.firstEntityWithComp
import com.dvail.clodiku.util.keyJustPressed
import com.dvail.clodiku.world.GameEngine

object UI {
    fun onClick(actor: Actor, handler: () -> Unit): Actor {
        actor.addListener(object : ClickListener() {
            override fun clicked(e: InputEvent?, x: Float, y: Float) { handler() }
        })
        return actor
    }
}

class GameUICore(mainGame: Game, world: GameEngine, eventQ: EventQueue) {
    val game = mainGame
    val world = world
    val eventQ = eventQ
    val player = firstEntityWithComp(world, Comps.Player)

    val subMenus = arrayOf("Equipment", "Inventory", "Skills", "Stats")

    val skin = Skin(Gdx.files.internal("./ui/uiskin.json"))
    val overlay = Table()
    val menus = Table()
    val mainMenu = VerticalGroup()
    val subMenuContainer = Container<Actor>()
    val subMenuActions = Table()
    val hpValue = Label("0", skin, "default-font", Color(0f, 1f, 0f, 1f))
    val mpValue = Label("0", skin, "default-font", Color(0f, 0f, 1f, 1f))

    val pauseScreen = Table()
    val continueButton = Label("Continue", skin, "default-font", Color(1f, 1f, 1f, 1f))
    val saveButton = Label("Save", skin, "default-font", Color(1f, 1f, 1f, 1f))
    val saveQuitButton = Label("Save & Quit", skin, "default-font", Color(1f, 1f, 1f, 1f))

    val stage = Stage()

    init {
        Gdx.input.inputProcessor = stage

        initHud()
        initMenus()
        initPauseScreen()
    }

    fun update(delta: Float) {
        updateHud()
        updateMenus()
        updatePauseScreen()

        if (keyJustPressed(BoundKeys.ToggleMenus)) toggleMenus()

        stage.act(delta)
        stage.draw()
    }

    private fun initHud() {
        val attributes = Table()
        attributes.add(hpValue).pad(5f)
        attributes.add(Label("HP", skin)).pad(5f)
        attributes.add(mpValue).pad(5f)
        attributes.add(Label("MP", skin)).pad(5f)
        attributes.left().bottom().pack()

        overlay.debug = true
        overlay.setFillParent(true)
        overlay.add(attributes)
        overlay.left().bottom().pack()

        stage.addActor(overlay)
    }

    private fun initMenus() {
        populateMenu()
        mainMenu.pad(10f)
        menus.debug = true
        menus.setFillParent(true)
        menus.row()
        menus.left()
        menus.add(mainMenu)
        menus.add(subMenuContainer)
        menus.add(subMenuActions)
        menus.pack()
        stage.addActor(menus)
    }

    private fun initPauseScreen() {
        pauseScreen.debug = true
        pauseScreen.isVisible = false
        pauseScreen.setFillParent(true)

        val items = Table()
        items.debug = true

        items.add(continueButton).row()
        items.add(saveButton).row()
        items.add(saveQuitButton).row()
        items.center().pack()
        pauseScreen.add(items)
        pauseScreen.center().pack()

        UI.onClick(continueButton, {
            pauseScreen.isVisible = false
            world.paused = false
        })

        UI.onClick(saveButton, {
            world.saveGame()
            println("Game saved")
        })

        UI.onClick(saveQuitButton, {
            println("Implement saving")
            println("Clean up all game resources")
            println("This just leaked a bunch of memory")
            game.screen.dispose()
            stage.dispose()
            game.screen = StartScreen(game)
        })

        stage.addActor(pauseScreen)
    }

    private fun updateHud() {
        val attributes = CompMapper.Attribute.get(player)
        hpValue.setText(attributes.hp.toString())
        mpValue.setText(attributes.mp.toString())
    }

    private fun updateMenus() {

    }

    private fun updatePauseScreen() {
        pauseScreen.isVisible = world.paused
    }

    private fun populateMenu() {
        subMenus.forEach { mainMenu.addActor(makeMenuButton(it)) }
    }

    private fun makeMenuButton(menuName: String): Label {
        val label = Label(menuName, skin)

        label.touchable = Touchable.enabled
        UI.onClick(label, { openSubMenu(menuName) })

        return label
    }

    private fun toggleMenus() {
        subMenuContainer.clear()
        subMenuContainer.actor = null
        subMenuActions.clear()
        menus.isVisible = !menus.isVisible
    }

    private fun openSubMenu(menuName: String) {
        subMenuContainer.clear()
        populateSubMenu(menuName)
    }

    private fun populateSubMenu(menuName: String) {
        val subTable = Table()
        subMenuContainer.actor = subTable

        when (menuName) {
            "Equipment" -> {
                val eq = CompMapper.Equipment.get(player).items

                eq.keys.forEach { key ->
                    val itemText = Label(CompMapper.Item.get(eq[key]).name, skin)
                    subTable.row()
                    subTable.add(Label(key.toString(), skin))
                    subTable.add(itemText).pad(5f)
                    subTable.add(Image(CompMapper.Renderable.get(eq[key]).texture))
                    UI.onClick(itemText, {
                        val ent = eq[key]
                        if (ent != null) populateActionMenu(menuName, ent)
                    })
                }
            }
            "Inventory" -> {
                val items = CompMapper.Inventory.get(player).items

                items.forEach { item ->
                    val itemText = Label(CompMapper.Item.get(item).name, skin)
                    val itemImg = Image(CompMapper.Renderable.get(item).texture)
                    subTable.row()
                    subTable.add(itemText)
                    subTable.add(itemImg)
                    UI.onClick(itemText, { populateActionMenu(menuName, item) })
                }
            }
        }
    }

    private fun populateActionMenu(menuName: String, itemEntity: Entity) {
        when (menuName) {
            "Equipment" -> {
                val eqText = Label("Remove", skin)
                subMenuActions.clear()
                subMenuActions.add(eqText)
                subMenuActions.row().pad(0f, 10f, 0f, 10f)
                eqText.touchable = Touchable.enabled
                UI.onClick(eqText, {
                    subMenuActions.clear()
                    subMenuContainer.clear()
                    eventQ.addEvent(EventType.UI, UnequipItemEvent(player, itemEntity))
                })
            }
            "Inventory" -> {
                val equipText = Label("Equip", skin)
                val dropText = Label("Drop", skin)

                subMenuActions.clear()
                subMenuActions.add(equipText)
                subMenuActions.row().pad(0f, 10f, 0f, 10f)
                subMenuActions.add(dropText)

                equipText.touchable = Touchable.enabled
                dropText.touchable = Touchable.enabled

                UI.onClick(equipText, {
                    subMenuActions.clear()
                    subMenuContainer.clear()
                    eventQ.addEvent(EventType.UI, EquipItemEvent(player, itemEntity))
                })

                UI.onClick(dropText, {
                    subMenuActions.clear()
                    subMenuContainer.clear()
                    eventQ.addEvent(EventType.UI, DropItemEvent(player, itemEntity))
                })
            }
        }
    }
}
