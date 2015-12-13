package com.dvail.klodiku.ui

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.*
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.dvail.klodiku.entities.CompMapper
import com.dvail.klodiku.entities.Comps
import com.dvail.klodiku.entities.EqSlot
import com.dvail.klodiku.events.*
import com.dvail.klodiku.util.BoundKeys
import com.dvail.klodiku.util.entitiesWithComps
import com.dvail.klodiku.util.firstEntityWithComp
import com.dvail.klodiku.util.keyJustPressed
import kotlin.jvm.internal.iterator

class GameUI(world: Engine, eventQ: EventQueue) {
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
    val stage = Stage()

    init {
        Gdx.input.inputProcessor = stage

        initHud()
        initMenus()
    }

    fun update(delta: Float) {
        updateHud()
        updateMenus()

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

    private fun updateHud() {
        val attributes = CompMapper.Attribute.get(player)
        hpValue.setText(attributes.hp.toString())
        mpValue.setText(attributes.mp.toString())
    }

    private fun updateMenus() {

    }

    private fun populateMenu() {
        subMenus.forEach { mainMenu.addActor(makeMenuButton(it)) }
    }

    private fun makeMenuButton(menuName: String): Label {
        val label = Label(menuName, skin)
        label.touchable = Touchable.enabled
        label.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                openSubMenu(menuName)
            }
        })
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
        // TODO subMenuContainer.setActor() ???
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
                    itemText.addListener(object : ClickListener() {
                        override fun clicked(event: InputEvent?, x: Float, y: Float) {
                            val ent = eq[key]
                            if (ent != null) populateActionMenu(menuName, ent)
                        }
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
                    itemText.addListener(object : ClickListener() {
                        override fun clicked(event: InputEvent?, x: Float, y: Float) {
                            populateActionMenu(menuName, item)
                        }
                    })

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
                eqText.addListener(object : ClickListener() {
                    override fun clicked(event: InputEvent?, x: Float, y: Float) {
                        subMenuActions.clear()
                        subMenuContainer.clear()
                        eventQ.addEvent(EventType.UI, UnequipItemEvent(player, itemEntity))
                    }
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

                equipText.addListener(object : ClickListener() {
                    override fun clicked(event: InputEvent?, x: Float, y: Float) {
                        subMenuActions.clear()
                        subMenuContainer.clear()
                        eventQ.addEvent(EventType.UI, EquipItemEvent(player, itemEntity))
                    }
                })

                dropText.addListener(object : ClickListener() {
                    override fun clicked(event: InputEvent?, x: Float, y: Float) {
                        subMenuActions.clear()
                        subMenuContainer.clear()
                        eventQ.addEvent(EventType.UI, DropItemEvent(player, itemEntity))
                    }
                })

            }
        }
    }
}
